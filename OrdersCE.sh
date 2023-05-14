#!/bin/bash
#Iniciar sesión mediante comandos curl con un dni y contraseña (rol no admin)
echo -e "****** Iniciamos sesion rol NO admin ******\n"
KEY=$(curl -s --data '{"dni":"23456733H","password":"123456"}' -X POST -H "content-type: application/json" http://localhost:9090/CentroEducativo/login -c cucu -b cucu)

#Consultamos lista de alumnos
echo -e "****** Lista de alumnos ******\n"
curl -s -X GET 'http://localhost:9090/CentroEducativo/alumnos?key='$KEY -H "accept: application/json" -c cucu -b cucu

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