# Documentación.
### Grupo 1 tardes: 3ti21_g01. 
- Julen Garcia, Santiago Chiappe, Nuria Manzano, Bianca Popa, Thomas Azevedo, Sonia Palomo y Joan Escutia.

## 1. Página de entrada y enlace a la operación:
La página de inicio de nuestra aplicación es "welcome.html".
```html
<!DOCTYPE html>
<html>
<head>
    <title>Notas Online - Welcome Page</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
    <div class="container">
        <h1 class="mt-5">Welcome to Notas Online!</h1>
        <p>Please select your role:</p>
        <a href="Teacher/Subjects" class="btn btn-primary">Teacher</a>
        <a href="Student/Subjects" class="btn btn-primary">Student</a>
    </div>
</body>
</html>
```
Para poder acceder a ella, hemos realizado la autentificación del login mediante un filtro para apache tomcat. Primeramente, es necesario agregar en tomcat/conf/tomcat-users.xml el siguiente código:

```xml
<role rolename="rolpro"/>
<role rolename="rolalu"/>
<user username="23456733H" password="123456" roles="rolpro"/>
<user username="10293756L" password="123456" roles="rolpro"/>
<user username="06374291A" password="123456" roles="rolpro"/>
<user username="65748923M" password="123456" roles="rolpro"/>
<user username="12345678W" password="123456" roles="rolalu"/>
<user username="23456387R" password="123456" roles="rolalu"/>
<user username="34567891F" password="123456" roles="rolalu"/>
<user username="93847525G" password="123456" roles="rolalu"/>
<user username="37264096W" password="123456" roles="rolalu"/>
```

De esta manera se mantiene el control desde tomcat de los usuarios autorizados para el acceso a nuestra aplicación web.

## 2. Autenticación web:
En el momento en el que se quiera acceder a nuestra aplicación web "localhost:8080/notas-online", se requerirá de una autentificación debido a que el login se realiza con basic auth de tomcat, donde se debe de rellenar el dni y password para su verificación.

![captura](https://raw.githubusercontent.com/mmnuria/imagesRested/main/inicio.png)

## 3. Login con CentroEducativo y mantenimiento de la sesión:
Una vez que se rellenan los campos anteriores entra en juego el filtro llamado "AuthenticationFilter.java" en el que se realiza el control que permite a tomcat que haga la autenticacion en nuestra aplicación y en la base de datos CentroEducativo.

```java
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		boolean isLoggedIn = (session != null && session.getAttribute("dni") != null);
		boolean isKeySet = (session.getAttribute("key") != null);

		if (!isLoggedIn && !isKeySet) {
			String login = httpRequest.getRemoteUser();

			if (login != null) {
				session.setAttribute("dni", login);
				session.setAttribute("password", "123456");

				try {
					// Prepare the request parameters
					String url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/login";
					String payload = "{ \"dni\": \"" + session.getAttribute("dni") + "\", \"password\": \""
							+ session.getAttribute("password") + "\"}";

					// Make the curl request
					URL urlObj = new URL(url);
					HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Accept", "text/plain");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setDoOutput(true);

					// Write the request payload
					DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
					outputStream.writeBytes(payload);
					outputStream.flush();
					outputStream.close();

					// Get the response status code
					int statusCode = connection.getResponseCode();

					// Read the response
					if (statusCode == HttpServletResponse.SC_OK) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line;
						StringBuilder responseContent = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							responseContent.append(line);
						}
						reader.close();

						// Set key as session attribute
						String key = responseContent.toString();
						session.setAttribute("key", key);
						
						String cookieString = connection.getHeaderFields().get("Set-Cookie").get(0);
						session.setAttribute("cookie", cookieString);
						
						String[] cookieParams = SetCookie(cookieString);
						Cookie cookie = new Cookie("JSESSIONID", cookieParams[0]);
						cookie.setPath(cookieParams[1]);
						// Set max age of cookie to 30 mins
						cookie.setMaxAge(30 * 60);
						httpResponse.addCookie(cookie);
						
						connection.disconnect();
					} else {
						httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Data authentication failed");
					}
					httpResponse.setStatus(statusCode);
				} catch (Exception e) {
					e.printStackTrace();
					httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Error establishing connection to database");
				}
			} else {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Basic authentication failed");
			}
		}

		// continues the filter chain
		// allows the request to reach the destination
		chain.doFilter(request, response);
	}
	
	String[] SetCookie(String cookie) 
	{
		String cookieSubs = cookie.substring(cookie.indexOf("=") + 1);
		String[] cookieSplited = cookieSubs.split("\\s+");
		
		String cookieValue = cookieSplited[0].substring(0, cookieSplited[0].indexOf(";"));
		
		String cookiePath = cookieSplited[1].substring(cookieSplited[1].indexOf("=") + 1);
		cookiePath = cookiePath.substring(0, cookiePath.indexOf(";"));
		
		cookieSplited[0] = cookieValue;
		cookieSplited[1] = cookiePath;
		
		return cookieSplited;
	}
}
```
- El usuario inicia una solicitud a la aplicación web como ("localhost:8080/notas-online").
- La solicitud es interceptada por el filtro de autenticación (AuthFilter).
- AuthFilter examina la solicitud y verifica si el usuario ya ha iniciado sesión y si se ha establecido la clave de sesión.
    - Si el __usuario no ha iniciado sesión__ y no se ha establecido la clave de sesión, AuthFilter obtiene el usuario remoto de HttpServletRequest.
    - Si el __usuario remoto existe__, AuthFilter establece los atributos "dni" y "password" en la sesión basándose en el usuario remoto.
    - Si el __usuario remoto es nulo__, AuthFilter establece el código de estado apropiado (401 No autorizado) y envía un mensaje de error que indica "Fallo en la autenticación básica".
- AuthFilter envía una solicitud de inicio de sesión POST a la base de datos de CentroEducativo, incluyendo los atributos "dni" y "password".
- La base de datos de CentroEducativo responde con un código de estado que indica el resultado de la autenticación.
    - Si la __autenticación es exitosa__, CentroEducativo envía la clave de respuesta a AuthFilter.
        - AuthFilter establece el atributo "key" en la sesión y almacena la información de la cookie recibida.
        - AuthFilter agrega la cookie JSESSIONID a HttpServletResponse.
    - Si la __autenticación falla__, AuthFilter establece el código de estado apropiado (401 No autorizado) y envía un mensaje de error que indica "Fallo en la autenticación de datos".
- HttpServletResponse se devuelve al usuario, con la respuesta adecuada.

A continuación se muestra un esquema resumen de su funcionamiento:

![captura](https://raw.githubusercontent.com/mmnuria/imagesRested/main/AuthenticationFilter.png)

## 4. Construcción y envío de las peticiones a CentroEducativo:
La conexión con la API CentroEducativo se realiza desde 3 perspectivas, por un lado, para la extracción del usuario, para la extracción de las asignaturas siendo rol alumno y para la extracción de las asignaturas siendo rol profesor.

Los servlets en la carpeta API actuan como un servidor REST, funcionan para hacer requests http para obtener informacion de la base de datos CentroEducativo y devolver la información para ser utilizada dinamicamente en nuestra aplicación web.

Los servlets Student_Subject y Teacher_Subject sirven como Controllers para acceder a las diferentes Views (MVC).

1. API para obtener los datos de usuario "API_User_Details.java"

    ```java
    /**
     * Servlet implementation class API_Person_details
     */
    @WebServlet("/API/User-Details")
    public class API_User_Details extends HttpServlet {
        private static final long serialVersionUID = 1L;
        
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException 
        {
            HttpSession session = request.getSession();
            if(session.getAttribute("dni") != null) 
            {
                String dni = (String) session.getAttribute("dni");
                String key = (String) session.getAttribute("key");
                String cookie = (String) session.getAttribute("cookie");
                
                try {
                    String url = null;
                    // Prepare the request parameters
                    String type = "";
                    if(request.isUserInRole("rolalu"))
                    {
                        type = "alumnos";
                    }
                    else if(request.isUserInRole("rolpro")) 
                    {
                        type = "profesores";
                    }
                    url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/" + type + "/" + dni + "?key=" + key;
                    
                    // Make the curl request
                    URL urlObj = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("cookie", cookie);

                    // Get the response status code
                    int statusCode = connection.getResponseCode();

                    // Read the response
                    if (statusCode == HttpServletResponse.SC_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder responseContent = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            responseContent.append(line);
                        }
                        reader.close();
                        connection.disconnect();

                        response.setContentType("application/json");
                        response.getWriter().write(responseContent.toString());

                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Data authentication failed");
                    }
                    response.setStatus(statusCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Error establishing connection to database");
                }
            }
            else 
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
            }
        }
    }

    ```
    Dicho código solicita la información del usuario con los datos de la sesión iniciada conectando con CentroEducativo.

2. API para obtener las asignaturas del rol alumno "API_Student_Subjects.java"

    ```java
    /**
    * Servlet implementation class API_Student_Subject
    */
    @WebServlet("/API/Student/Subjects")
    public class API_Student_Subjects extends HttpServlet {
        private static final long serialVersionUID = 1L;
        
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException 
        {
            if(request.isUserInRole("rolalu")) 
            {
                HttpSession session = request.getSession();
                String dni = (String) session.getAttribute("dni");
                String key = (String) session.getAttribute("key");
                String cookie = (String) session.getAttribute("cookie");
                
                try {
                    String url = null;
                    // Prepare the request parameters
                    url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/alumnos/" + dni + "/asignaturas?key=" + key;
                    
                    //String cookie = response.getHeader("Set-Cookie");
                    
                    // Make the curl request
                    URL urlObj = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("cookie", cookie);

                    // Get the response status code
                    int statusCode = connection.getResponseCode();

                    // Read the response
                    if (statusCode == HttpServletResponse.SC_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder responseContent = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            responseContent.append(line);
                        }
                        reader.close();
                        connection.disconnect();

                        response.setContentType("application/json");
                        response.getWriter().write(responseContent.toString());

                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Data authentication failed");
                    }
                    response.setStatus(statusCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Error establishing connection to database");
                }
            }
            else 
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
            }
        }
    }
    ```
    Similar a la anterior, pero para el caso concreto de cuando eres rol alumno, para que, de esa manera se pueda obtener sus asignaturas.

3. API para obtener las asignaturas que imparte con rol profesor "API_Teacher_Subjects.java"

    ```java
    @WebServlet("/API/Teacher/Subjects")
    public class API_Teacher_Subjects extends HttpServlet {
        private static final long serialVersionUID = 1L;

        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException 
        {
            if(request.isUserInRole("rolpro")) 
            {
                HttpSession session = request.getSession();
                String dni = (String) session.getAttribute("dni");
                String key = (String) session.getAttribute("key");
                String cookie = (String) session.getAttribute("cookie");
                
                try {
                    String url = null;
                    // Prepare the request parameters
                    url = DatabaseConfig.CENTRO_EDUCATIVO_URL + "/profesores/" + dni + "/asignaturas?key=" + key;
                    
                    //String cookie = response.getHeader("Set-Cookie");
                    
                    // Make the curl request
                    URL urlObj = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("cookie", cookie);

                    // Get the response status code
                    int statusCode = connection.getResponseCode();

                    // Read the response
                    if (statusCode == HttpServletResponse.SC_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder responseContent = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            responseContent.append(line);
                        }
                        reader.close();
                        connection.disconnect();

                        response.setContentType("application/json");
                        response.getWriter().write(responseContent.toString());

                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Data authentication failed");
                    }
                    response.setStatus(statusCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Error establishing connection to database");
                }
            }
            else 
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
            }
        }
    }
    ```
    Funciona exactamente igual que el de rol alumno, pero para el caso de que el usuario es de rol profesor.

Además, para poder generar el certificado del alumno con sus asignaturas correspondientes es necesario el código siguiente del archivo "Student_Report.java"

```java
@WebServlet("/Student/Report")
public class Student_Report extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		{
			if(request.isUserInRole("rolalu")) 
			{
				RequestDispatcher rd = request.getRequestDispatcher("../student_report.html");
		        rd.include(request, response);
			}
			else 
			{
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Action unauthorized.");
			}
		}
	}
}
```
Que comprobando que es un alumno, responde con la página html adecuada.

## 5. Interpretación de las respuestas de CentroEducativo:

CentroEducativo nos proporciona la información solicitada para después poder usarla y mostrarla en nuestra página. Nos apollamos en los archivos siguientes para posteriormente poder mostrar el contenido correctamente en html.

A partir de estos scripts utilizamos ajax a un endpoint de nuestro API para obtener el get de la base de datos, ese contenido que obtenemos lo insertamos en el html.

1. Información extra del usuario conectado "user_details.js"
    ```js
    function fetchContactDetails() {
        rootUrl = `${window.location.origin}/notas-online`;
        
        // Make an AJAX request to the servlet endpoint
        fetch(`${rootUrl}/API/User-Details`)
            .then(response => response.json()) // Parse the response as JSON
            .then(contact => {
                const contactNameElement = document.getElementById('contact-name');
                contactNameElement.innerHTML = `${contact.nombre} ${contact.apellidos}`;
            })
            .catch(error => {
                console.error('Error fetching contact details:', error);
            });
    }

    fetchContactDetails();
    ```
    Este código convierte la información del usuario en un json y lo inserta en la página.

2. Información de las asignaturas del alumno "student_subjects.js"
    ```js
    function fetchSubjects() {
        rootUrl = `${window.location.origin}/notas-online`;

        // Make an AJAX request to the servlet endpoint
        fetch(`${rootUrl}/API/Student/Subjects`)
            .then(response => response.json()) // Parse the response as JSON
            .then(subjects => {
                // Fetch the list of all subjects as JSON from the new servlet call
                fetch(`${rootUrl}/API/Subjects`)
                    .then(response => response.json())
                    .then(allSubjects => {
                        const subjectListElement = document.getElementById('subject-list');
                        const subjectTabsElement = document.getElementById('subject-tabs');
                        const subjectContentElement = document.getElementById('subject-content');

                        // Clear any existing subjects and tabs
                        subjectListElement.innerHTML = '';
                        subjectTabsElement.innerHTML = '';
                        subjectContentElement.innerHTML = '';

                        // Iterate over the subjects and create a card for each subject
                        subjects.forEach(subject => {
                            // Find the subject with matching acronimo
                            const matchedSubject = allSubjects.find(item => item.acronimo === subject.asignatura);

                            if (matchedSubject) {
                                const subjectCard = document.createElement('div');
                                subjectCard.innerHTML = `
                                    <a href="#subject-${subject.asignatura}" class="subject-link" data-toggle="tab" data-subject="${subject.asignatura}">${matchedSubject.nombre}</a>
                                `;
                                subjectListElement.appendChild(subjectCard);

                                // Create the tab for the subject
                                const tabLink = document.createElement('li');
                                tabLink.classList.add('nav-item');
                                tabLink.innerHTML = `
                                    <a class="nav-link" id="${subject.asignatura}-tab" data-toggle="tab" href="#subject-${subject.asignatura}">${subject.asignatura}</a>
                                `;
                                subjectTabsElement.appendChild(tabLink);

                                // Create the content for the subject tab
                                const tabContent = document.createElement('div');
                                tabContent.classList.add('tab-pane', 'fade');
                                tabContent.id = `subject-${subject.asignatura}`;
                                subjectContentElement.appendChild(tabContent);
                            }
                        });

                        // Add click event listeners to the subject links
                        const subjectLinks = document.querySelectorAll('.subject-link');
                        subjectLinks.forEach(subjectLink => {
                            subjectLink.addEventListener('click', function(subject) {
                                return function(event) {
                                    event.preventDefault(); // Prevent the default link behavior
                                    const subjectName = subject.getAttribute('data-subject');
                                    const subjectAcronym = subject.getAttribute('href').substring(1); // Remove the leading #

                                    // Show the tab for the selected subject
                                    const tabElement = document.getElementById(subjectAcronym);
                                    const tabContainerElement = document.querySelector('.tab-content');
                                    const tabPaneElements = tabContainerElement.querySelectorAll('.tab-pane');

                                    // Show the selected tab and hide other tabs
                                    tabPaneElements.forEach(tabPane => {
                                        if (tabPane.id === subjectAcronym) {
                                            tabPane.classList.add('active', 'show');
                                        } else {
                                            tabPane.classList.remove('active', 'show');
                                        }
                                    });

                                    // Activate the selected tab navigation link
                                    const tabNavLinks = document.querySelectorAll('.nav-link');
                                    tabNavLinks.forEach(tabNavLink => {
                                        if (tabNavLink.getAttribute('href') === `#${subjectAcronym}`) {
                                            tabNavLink.classList.add('active');
                                        } else {
                                            tabNavLink.classList.remove('active');
                                        }
                                    });

                                    // Clear the contents of the tab
                                    tabElement.innerHTML = '';

                                    // TODO: Add code to display other information for the selected subject
                                    // Retrieve and display the grade
                                    const gradeElement = document.createElement('p');
                                    gradeElement.innerText = `Nota: ${subject.nota}`;
                                    tabElement.appendChild(gradeElement);
                                };
                            }(subjectLink));
                        });
                    })
                    .catch(error => {
                        console.error('Error fetching all subjects:', error);
                    });
            })
            .catch(error => {
                console.error('Error fetching subjects:', error);
            });
    }

    fetchSubjects();
    ```
    Ésta información obtenida como un json, se pasará más tarde a un archivo html que le dará el formato apropiado para que nuestra aplicación web continúe con una buena estructura.

3. Información de las asignaturas que imparte el profesor "teacher_subjects.js"

    ```js
    function fetchSubjects() {
        rootUrl = `${window.location.origin}/notas-online`;
        
        // Make an AJAX request to the servlet endpoint
        fetch(`${rootUrl}/API/Teacher/Subjects`)
            .then(response => response.json()) // Parse the response as JSON
            .then(subjects => {
                const subjectListElement = document.getElementById('subject-list');
                const subjectTabsElement = document.getElementById('subject-tabs');
                const subjectContentElement = document.getElementById('subject-content');

                // Clear any existing subjects and tabs
                subjectListElement.innerHTML = '';
                subjectTabsElement.innerHTML = '';
                subjectContentElement.innerHTML = '';

                // Iterate over the subjects and create a card for each subject
                subjects.forEach(subject => {
                    const subjectCard = document.createElement('div');
                    subjectCard.innerHTML = `
                        <a href="#subject-${subject.acronimo}" class="subject-link" data-toggle="tab" data-subject="${subject.nombre}">${subject.nombre}</a>
                    `;
                    subjectListElement.appendChild(subjectCard);

                    // Create the tab for the subject
                    const tabLink = document.createElement('li');
                    tabLink.classList.add('nav-item');
                    tabLink.innerHTML = `
                        <a class="nav-link" id="${subject.acronimo}-tab" data-toggle="tab" href="#subject-${subject.acronimo}">${subject.acronimo}</a>
                    `;
                    subjectTabsElement.appendChild(tabLink);

                    // Create the content for the subject tab
                    const tabContent = document.createElement('div');
                    tabContent.classList.add('tab-pane', 'fade');
                    tabContent.id = `subject-${subject.acronimo}`;
                    subjectContentElement.appendChild(tabContent);
                });

                // Add click event listeners to the subject links
                const subjectLinks = document.querySelectorAll('.subject-link');
                subjectLinks.forEach(subjectLink => {
                    subjectLink.addEventListener('click', function(event) {
                        event.preventDefault(); // Prevent the default link behavior
                        const subjectName = this.getAttribute('data-subject');
                        const subjectAcronym = this.getAttribute('href').substring(1); // Remove the leading #

                        // Show the tab for the selected subject
                        const tabElement = document.getElementById(subjectAcronym);
                        const tabContainerElement = document.querySelector('.tab-content');
                        const tabPaneElements = tabContainerElement.querySelectorAll('.tab-pane');

                        // Show the selected tab and hide other tabs
                        tabPaneElements.forEach(tabPane => {
                            if (tabPane.id === subjectAcronym) {
                                tabPane.classList.add('active', 'show');
                            } else {
                                tabPane.classList.remove('active', 'show');
                            }
                        });

                        // Activate the selected tab navigation link
                        const tabNavLinks = document.querySelectorAll('.nav-link');
                        tabNavLinks.forEach(tabNavLink => {
                            if (tabNavLink.getAttribute('href') === `#${subjectAcronym}`) {
                                tabNavLink.classList.add('active');
                            } else {
                                tabNavLink.classList.remove('active');
                            }
                        });

                        // TODO: Add code to display other information for the selected subject
                        console.log('Clicked subject:', subjectName);
                    });
                });
            })
            .catch(error => {
                console.error('Error fetching subjects:', error);
            });
    }

    fetchSubjects();

    ```
    Recupera la información de las asignaturas del profesor para mostrarlas mediante un html.

4. Información del certificado del alumno para poder imprimirselo "student_report.js"

    ```js
    $(document).ready(function() {
	// Get the subjects data from the URL parameters
	const urlParams = new URLSearchParams(window.location.search);
	const subjectsParam = urlParams.get('subjects');
	const subjects = JSON.parse(subjectsParam);

	// Populate the table with subjects
	const reportContent = document.getElementById('report-content');
	subjects.forEach(subject => {
		const row = document.createElement('tr');
		row.innerHTML = `
	            <td>${subject.subjectAcronym}</td>
	            <td>${subject.subjectName}</td>
	            <td>${subject.grade}</td>
	        `;
		reportContent.appendChild(row);
	});

	// Add the date
	var date = new Date();
	var day = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	var formattedDate = day + '/' + month + '/' + year;
	document.getElementById('date').innerText = formattedDate;
    });
    ```
    Recuperamos la información de los parámetros y los mostramos en una tabla, de esa manera, no es necesario volver a llamar a CentroEducativo debido a que en la página anterior donde muestra las asignaturas del alumno tenemos dicha información.

## 6. Construcción y retorno de las páginas HTML de respuesta:
Los archivos expuestos en los anteriores aparatados se requieren para poder obtener la información que se necesita mostrar con la ayuda de archivos html. Éstos archivos obtienen páginas de retorno que completan la aplicación web.

1. Welcome.html

    ```html
    <!DOCTYPE html>
    <html>
    <head>
        <title>Notas Online - Welcome Page</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    </head>
    <body>
        <div class="container">
            <h1 class="mt-5">Welcome to Notas Online!</h1>
            <p>Please select your role:</p>
            <a href="Teacher/Subjects" class="btn btn-primary">Teacher</a>
            <a href="Student/Subjects" class="btn btn-primary">Student</a>
        </div>
    </body>
    </html>
    ```

    Página de inicio para nuestra aplicación web.

    ![captura](https://raw.githubusercontent.com/mmnuria/imagesRested/main/welcome.png)

2. teacher_subjects.html

    ```html
        <!DOCTYPE html>
    <html lang="en">

    <head>
    <meta charset="UTF-8">
    <title>Notas OnLine - Home</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet"
        href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="../css/style.css">
    <link rel="shortcut icon" href="#" />
    </head>

    <body>

        <div class="container">
            <div class="row mt-3">
                <div class="col-md-12">
                    <div class="float-left mt-2">
                        <p id="placeholder-text" class="text-muted">You are logged in
                            as <span id="contact-name">[full name]</span></p>
                    </div>
                    <button class="btn btn-primary float-right mt-2" id="logout-button">Logout</button>
                </div>
            </div>
            <div class="row mt-4">
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-header">
                            <h4 class="text-center">My subjects</h4>
                        </div>
                        <div class="card-body">
                            <div class="col" id="subject-list">
                                <!-- Subject list will be generated here dynamically -->
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-8">
                    <ul class="nav nav-tabs" id="subject-tabs">
                        <!-- Subject tabs will be generated here dynamically -->
                    </ul>
                    <div class="tab-content" id="subject-content">
                        <!-- Subject content will be displayed here dynamically -->
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
        <script
            src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
        <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

        <!-- Custom JavaScript -->
        <script src="../js/teacher/teacher_subjects.js"></script>
        <script src="../js/user_details.js"></script>
        <script>
            $(document).ready(function() {
                // Logout button click event handler
                $("#logout-button").click(function() {
                    window.location.href = "../Logout";
                });
            });
        </script>
    </body>

    </html>
    ```
    Una vez que se inicia sesión como profesor y en la página de welcome se selecciona entrar como teacher (y se verifica con nuestro filtro que ese usuario es de rol profesor), se accede a la página de las asignaturas que imparte.

    ![captura](https://raw.githubusercontent.com/mmnuria/imagesRested/main/teacher.png)

    Se ha iniciado sesión con el usuario: 06374291A y password: 123456

3. student_subjects.html

    ```html
    <!DOCTYPE html>
    <html lang="en">

    <head>
    <meta charset="UTF-8">
    <title>Notas OnLine - Home</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet"
        href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="../css/style.css">
    <link rel="shortcut icon" href="#" />
    </head>

    <body>

        <div class="container">
            <div class="row mt-3">
                <div class="col-md-12">
                    <div class="float-left mt-2">
                        <p id="placeholder-text" class="text-muted">
                            You are logged in as <span id="contact-name">[full name]</span>
                        </p>
                    </div>
                    <button class="btn btn-primary float-right mt-2" id="logout-button">Logout</button>
                    
                </div>
            </div>
            <div class="row mt-4">
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-header">
                            <h4 class="text-center">My subjects</h4>
                        </div>
                        <div class="card-body">
                            <div class="col" id="subject-list">
                                <!-- Subject list will be generated here dynamically -->
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-8">
                    <ul class="nav nav-tabs" id="subject-tabs">
                        <!-- Subject tabs will be generated here dynamically -->
                    </ul>
                    <div class="tab-content" id="subject-content">
                        <!-- Subject content will be displayed here dynamically -->
                    </div>
                </div>
            </div>
            <a class="btn btn-primary float-right mt-2 text-white" id="show-report-button">Show report</a>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
        <script
            src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
        <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

        <!-- Custom JavaScript -->
        <script src="../js/student/student_subjects.js"></script>
        <script src="../js/user_details.js"></script>
        <script>
            $(document).ready(function() {
                // Logout button click event handler
                $("#logout-button").click(function() {
                    window.location.href = "../Logout";
                });
            });
        </script>
    </body>

    </html>
    ```
    Una vez que se inicia sesión como alumno y en la página de welcome se selecciona entrar como student (y se verifica con nuestro filtro que ese usuario es de rol alumno), se accede a la página de las asignaturas a las que está matriculado.

    ![captura](https://raw.githubusercontent.com/mmnuria/imagesRested/main/student.png)

    Se ha iniciado sesión con el usuario: 23456387R y password: 123456

4. student_report.html

    ```html
    <!DOCTYPE html>
    <html lang="en">

    <head>
    <meta charset="UTF-8">
    <title>Notas OnLine - Report</title>
    <link rel="stylesheet"
        href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/style.css">
    <link rel="shortcut icon" href="#" />
    </head>

    <body>
        <div class="container">
            <div class="row text-center bg-primary text-white mb-5">
                <h1 class="display-4">DEW - CentroEducativo</h1>
            </div>
            <div class="text-center display-3 mb-5">Certificado sin validez
                académica</div>
            <div class="row mb-5">
                <div class="col-md-8">
                    <strong>DEW - Centro Educativo</strong> certifica que <strong><span
                        id="contact-name">[full name]</span></strong> con DNI <span id="contact-dni">[full
                        dni]</span> matriculado/a en el curso 2022/23, ha obtenido las
                    calificaciones que se muestran en la siguiente tabla.
                </div>
                <div class="col-md-4">
                    <img src="https://via.placeholder.com/300" alt="Placeholder Image"
                        width="300px">
                </div>
            </div>
            <div class="row mb-5">
                <div class="col-md-12">
                    <table class="table">
                        <thead class="bg-primary text-white">
                            <tr>
                                <th scope="col">Acrónimo</th>
                                <th scope="col">Asignatura</th>
                                <th scope="col">Calificación</th>
                            </tr>
                        </thead>
                        <tbody id="report-content">
                            <!-- The report will be generated here -->
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="text-right">
                En Valencia, a <span id="date"></span>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
        <script
            src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
        <script
            src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
        <script src="../js/user_details.js"></script>
        <script src="../js/student/student_report.js"></script>
    </body>

    </html>
    ```
    Esta página muestra el certificado de un alumno con sus asignaturas matriculadas con sus respectivas notas (no se muestran notas porque actualmente no hay).

    ![captura](https://raw.githubusercontent.com/mmnuria/imagesRested/main/student_report.png)

    Actualmente no se ha implementado la imagen, porque será implementación para el siguiente hito.