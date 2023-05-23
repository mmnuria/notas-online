#!/bin/bash
#Iniciar sesión mediante comandos curl con un dni y contraseña (rol no admin)
echo -e "****** Iniciamos sesion rol NO admin ******\n"
KEY=$(curl -s --data '{"dni":"23456733H","password":"123456"}' -X POST -H "content-type: application/json" http://localhost:9090/CentroEducativo/login -c cucu -b cucu)

#Consultamos lista de alumnos
echo -e "****** Lista de alumnos ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY -H "accept: application/json" -c cucu -b cucu

#Consultamos lista de alumnos y asignaturas
echo -e "\n\n****** Lista de alumnos y asignaturas ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/alumnosyasignaturas?key='$KEY \ - H "accept: application/json" -c cucu -b cucu

#Consultamos lista de alumnos y calificaciones de DEW
echo -e "\n\n****** Lista de alumnos y calificaciones de DEW ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/asignaturas/DEW/alumnos?key='$KEY \ - H "accept: application/json" -c cucu -b cucu

#Consultamos lista de profesores de la asignatura DEW
echo -e "\n\n****** Lista de profesores de la asignatura DEW******\n"
curl -X GET 'http://localhost:9090/CentroEducativo/asignaturas/DEW/profesores?key='$KEY -H "accept: application/json" -c cucu -b cucu

#Se observa que no contienen notas en dicha asignatura, por ello añadiremos al usuario 12345678W la nota de 9
#Para ello es necesario iniciar sesion
#echo -e "\n\n****** Iniciamos sesion del profesor de DEW ******\n"
#KEY1=$(curl -s --data '{"dni": "23456733H","password":"123456"}' -X POST -H "content-type: application/json" 'http://localhost:9090/CentroEducativo/login' -c cucu -b cucu)
#KEY2=$(curl -s --data '{"dni":"111111111","password":"654321"}' -X POST -H "content-type: application/json" 'http://localhost:9090/CentroEducativo/login' -c cucu -b cucu)

#echo -e "\n\n****** Añadimos la nota de un 9 al alumno 12345678W de la asignatura DEW ******\n"
#curl -X PUT -H "accept: text/plain" -H "Content-Type: application/json" -d "{9}" 'http://localhost:9090/CentroEducativo/alumnos/12345678W/asignaturas?key='$KEY2'/DEW' -c cucu -b cucu -v

#Para crear un alumno nuevo es necesario ser ROL admin
echo -e "****** Creamos un nuevo alumno ******\n"
KEY2=$(curl -s --data '{"dni":"111111111","password":"654321"}' -X POST -H "content-type: application/json" 'http://localhost:9090/CentroEducativo/login' -c cucu -b cucu)

curl -s --data '{"apellidos": "Nuevo", "dni": "33445566X", "nombre": "Alumno","password": "123456789"}' -X POST -H "content-type: application/json" 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY2 -c cucu -b cucu

echo -e "\n****** Obtenemos de nuevo los alumnos ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY2 -H "accept: application/json" -c cucu -b cucu

echo -e "\n\n****** El ultimo alumno de la tabla es el nuevo alumno creado ******\n"

#Borramos el alumno que acabamos de añadir
#echo -e "\n\n****** Borramos el alumno que acabamos de añadir ******\n"
#curl -X DELETE -H "content-type: application/json" -d '{"dni": "33445566X"}' 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY2 -c cucu -b cucu
#curl -X DELETE 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY2'/33445566X' -H  "Content-Type: application/json" -d '{"dni": "33445566X"}' -c cucu -b cucu -v
#curl -X DELETE 'http://localhost:9090/CentroEducativo/alumnos/33445566X' -H  "accept: text/plain" -c cucu -b cucu


#Mostramos alumnos para observar su borrado
echo -e "\n\n****** Lista de alumnos sin el ultimo añadido ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY2 -H "accept: application/json" -c cucu -b cucu

#Añadimos una asignatura
echo -e "\n\n****** Añadimos la asignatura EDA ******\n"
curl -s --data '{  "acronimo": "EDA",  "creditos": 6,  "cuatrimestre": "Segundo",  "curso": 3,  "nombre": "Estructura de datos"}' -X POST -H "content-type: application/json" 'http://localhost:9090/CentroEducativo/asignaturas?key='$KEY2 -c cucu -b cucu
#Consultamos las asignaturas
echo -e "\n\n****** Consultamos las asignaturas y aparecerá EDA ******\n"
curl -X GET 'http://localhost:9090/CentroEducativo/asignaturas?key='$KEY2 -H  "accept: application/json" -c cucu -b cucu

#Añadimos un profesor de esa asignatura, como hay que ser administrador usamos la variable KEY1 que guarda la sesion

echo -e "\n****** Añadimos un profesor (Juan Gonzalez Garcia) ******\n"

curl -s --data '{"apellidos": "Gonzalez Garcia",  "dni": "209566378V",  "nombre": "Juan",  "password": "987654321"}' -X POST -H "content-type: application/json" 'http://localhost:9090/CentroEducativo/profesores?key='$KEY2 -c cucu -b cucu

echo -e "\n****** Añadimos ese profesor a la asignatura EDA ******\n"
curl -X POST 'http://localhost:9090/CentroEducativo/profesores/209566378V/asignaturas?key='$KEY2 -H  "accept: text/plain" -H  "Content-Type: application/json" -d "EDA" -c cucu -b cucu

echo -e "\n\n****** Mostramos lista de profesores con el nuevo añadido ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/profesores?key='$KEY2 -H "accept: application/json" -c cucu -b cucu
echo -e "\n\n****** El ultimo alumno de la tabla es el nuevo profesor creado ******\n"

echo -e "\n\n****** Mostramos lista de asignaturas que imparte dicho profesor (tiene que impartir EDA) ******\n"
curl -X GET 'http://localhost:9090/CentroEducativo/profesores/209566378V/asignaturas?key='$KEY2 -H  "accept: application/json" -c cucu -b cucu

echo -e "\n\n****** Se observa que el profesor añadido imparte (como debe de ser) la asignatura creada EDA  ******\n"
