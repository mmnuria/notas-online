#!/bin/bash
#Iniciar sesión mediante comandos curl con un dni y contraseña (rol no admin)
echo -e "****** Iniciamos sesion rol NO admin ******\n"
KEY=$(curl -s --data '{"dni":"23456733H","password":"123456"}' -X POST -H "content-type: application/json" http://localhost:9090/CentroEducativo/login -c cucu -b cucu)

#Consultamos lista de alumnos
echo -e "****** Lista de alumnos ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY -H "accept: application/json" -c cucu -b cucu

#Consultamos lista de alumnos y asignaturas
echo -e "****** Lista de alumnos y asignaturas ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/alumnosyasignaturas?key='$KEY \ - H "accept: application/json" -c cucu -b cucu

#Consultamos lista de alumnos y calificaciones de DEW
echo -e "****** Lista de alumnos y calificaciones de DEW ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/asignaturas/DEW/alumnos?key='$KEY \ - H "accept: application/json" -c cucu -b cucu

#Consultamos lista de profesores
echo -e "\n\n****** Lista de profesores ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/profesores?key='$KEY -H "accept: application/json" -c cucu -b cucu

#Añadimos un alumno, pero antes debemos de ser administrador
echo -e "\n\n****** Iniciamos sesion rol admin ******\n"
KEY1=$(curl -s --data '{"dni":"111111111","password":"654321"}' -X POST -H "content-type: application/json" http://localhost:9090/CentroEducativo/login -c cucu -b cucu)
echo -e "****** Creamos un nuevo alumno ******\n"
curl -s --data '{"apellidos": "Nuevo", "dni": "33445566X", "nombre": "Alumno","password": "123456789"}' -X POST -H "content-type: application/json" 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY1 -c cucu -b cucu
echo -e "\n****** Obtenemos de nuevo los alumnos ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY1 -H "accept: application/json" -c cucu -b cucu
echo -e "\n****** El ultimo alumno de la tabla es el nuevo alumno creado ******\n"

#Borramos el alumno que acabamos de añadir
echo -e "\n\n****** Borramos un alumno ******\n"
curl -X DELETE 'http://localhost:9090/CentroEducativo/alumnos/33445566X' -H  "accept: text/plain" -c cucu -b cucu

#Añadimos una asignatura
echo -e "\n\n****** Añadimos una asignatura ******\n"
curl -s --data '{  "acronimo": "EDA",  "creditos": 6,  "cuatrimestre": "Segundo",  "curso": 3,  "nombre": "Estructura de datos"}' -X POST -H "content-type: application/json" 'http://localhost:9090/CentroEducativo/asignaturas?key='$KEY1 -c cucu -b cucu
#Consultamos las asignaturas
echo -e "\n\n****** Consultamos las asignaturas ******\n"
curl -X GET 'http://localhost:9090/CentroEducativo/asignaturas?key='$KEY1 -H  "accept: application/json" -c cucu -b cucu

#Añadimos un profesor de esa asignatura, pero antes debemos ser administrador
echo -e "\n\n****** Iniciamos sesion rol admin ******\n"
KEY1=$(curl -s --data '{"dni":"111111111","password":"654321"}' -X POST -H "content-type: application/json" http://localhost:9090/CentroEducativo/login -c cucu -b cucu)
echo -e "\n\n****** Añadimos un profesor ******\n"
curl -X POST 'http://localhost:9090/CentroEducativo/profesores' -H  "accept: text/plain" -H  "Content-Type: application/json" -d '{  "apellidos": "Gonzalez,Garcia",  "dni": "209566378V",  "nombre": "Juan",  "password": "987654321"}"
