#!/bin/bash

echo "start docker-compose up: ubuntu"
sudo docker rm -f $(sudo docker ps -qa)
sudo docker-compose -f /home/ubuntu/project01/docker-compose.prod.yml up --build -d
