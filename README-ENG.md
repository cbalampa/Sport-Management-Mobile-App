# Sport Management Mobile App [![gr](https://img.shields.io/badge/lang-gr-blue.svg)](https://github.com/cbalampa/Sport-Management-Mobile-App/blob/main/README.md) [![eng](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/cbalampa/Sport-Management-Mobile-App/blob/main/README-ENG.md)

Contents
=================
* [App Description](#app-description)
* [Technologies](#technologies)
* [Database](#database)
* [Setup](#setup)
* [Features](#features)
* [Preview](#preview)

## App Description
Sport management application for smart devices. Two types of databases are used: a local one (Room API) in which the information of athletes, teams & sports is stored and a remote one (Firebase Firestore), where games (matches) and user accounts are saved.

## Technologies
The following technologies were used to create the project:
- Java
- Android Studio
- Room API
- Firebase Firestore
- SQL

## Setup
You can try the app by simply downloading the files you will find in the link below:
[https://drive.google.com/drive/folders/1-XG4xadguRQwnNyhNfvz2mIKwyeE4LWS?usp=sharing](https://drive.google.com/drive/folders/1-XG4xadguRQwnNyhNfvz2mIKwyeE4LWS?usp=sharing)

## Database
Consists of five tables that are related to each other using foreign keys as shown in the image below.


![Database Diagram](https://user-images.githubusercontent.com/73292440/134400105-bb5b040d-0687-437e-9ff1-42801c3a875d.png)

In more detail, three tables are stored locally on the mobile device, while the other two are stored on a Google Cloud server. Note: table "Games" takes data from every table in the local database.

## Features
Login/Sign Up
- Login to personal account
- Create new account

Manage Athlete
- Insert an athlete to local database
- Edit/update athlete's information
- Delete an athlete from local database

Manage Team
- Insert a team to local database
- Edit/update team's information
- Delete a team from local database

Manage Sport
- Insert a sport to local database
- Edit/update sport's information
- Delete a sport from information

Manage Games
- Insert a game to remote database
- Edit/update game's information
- Delete a game from remote database

Display Statistics
- Count athletes in local database
- Number of athletes per sport
- Average age of athletes 


## Preview
<p align="center">
<img src="https://user-images.githubusercontent.com/73292440/134453091-824c9763-c1ae-4b2b-bf99-f37472b81426.gif" alt="drawing" width="300"/>
</p>

