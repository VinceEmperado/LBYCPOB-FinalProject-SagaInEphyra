# LBYCPOB-FinalProject-SagaInEphyra

# Saga in Ephyra: A Bullet Hell Game

# Team Members:
Vince Delphine C. Emperado (E21) - VinceEmperado
Jude Daniel F. Tungol (E23) - mzkakym
Matthew B. Ylanan (E23) - matthewylanan-sirius

# Problem Statement and Goals

Many people in the world find it difficult, if not are completely unable to, focus on anything and are easily distracted by their surroundings. Bullet hell games such as Undertale, the Touhou Project, and Cuphead force players to focus on dodging bullets, watch out for how many hitpoints they have left, and defeat the enemy. This project aims to assist people who find it difficult to focus to manage their focus and apply what they can do in bullet hell games into the real world, such as focusing on meetings, lesson videos, and coursework among others.

# Description

This project is inspired by other bullet hell games such as UNDERTALE and DELTARUNE by Toby Fox, Cuphead by Studio MDHR, and the Touhou Project by ZUN. It will be a boss-rush type bullet hell, where the goal of the players is to defeat bosses while attacking them and dodging their bullets. Players will only have three hitpoints, with some "1-UP" items that increase this number by 1. Every time the player gets hit, there will be invincibility frames for a set number of seconds that will make bullets unable to hit the character.

# Target Users

The main target audience of this project are those who suffer from ADHD or other conditions that make it difficult for them to focus on something like meetings and the completion of their coursework.

# Core OOP Concepts

- Encapsulation - Private attributes for playable characters and enemy characters. Playable characters will have basic attacks, a skill, and an ultimate.
- Inheritance - Character -> NameOfSpecificCharacter
- Polymorphism - Abstract class Character will have abstract methods basic attack, skill, and ultimate, which will be overriden when used by specific characdters
- Abstraction - Interfaces Playable and Enemy will categorize characters into two: ones that can be harmed by basic attacks, skills, and ultimates from the playables, as well as playable characters that will only have a single-digit number of hitpoints.

# Initial Class Ideas

- Character - This is an abstract class that will have attributes attack, skill, ultimate, hitpoints, and hitbox.
- Playable - This will be an interface, and it will have methods that allow users to control the character. Classes that implement this will start with 3 hitpoints.
- Enemy - This will be an interface, and it will have methods that allow a character to harm the playable character, move randomly, and will have attack patterns. The classes that implement this will have an HP bar which the players must deplete by attacking them.
- Bullet - These will be the objects that will harm the playable character if it makes contact with their hitbox. There are different types of bullets, such as ones that follow the player, laser beams, and many others.

![Image of Touhou 7: Perfect Cherry Blossom, one of the inspirations for this project](https://upload.wikimedia.org/wikipedia/en/a/a4/TH07_PCB_Interface.jpg)

NOTE: This image is from the 7th Touhou Project game by ZUN called Touhou 7: Perfect Cherry Blossom. The game series is one of the main inspirations for this project.

# User Stories

- As a person who struggles to focus, I want to play the bullet hell stages so that I can practice maintaining my attention span.
- As a player, I want the game to gradually become more difficult.
- As a player, I want clear visual feedback whenever I get hit so that I can immediately recognize my mistakes and improve for my next run.
- As a player, I want to see my previous scores so I can monitor my improvement.
- As a player, I want bosses with unique attack patterns so that I stay engaged while training my focus.

# Core Features
- Leaderboard system using local scores
- Account system for saving scores for multiple users
