# Thrive

> CircleCI status:
>
> `master` [![CircleCI](https://circleci.com/gh/FilipMalczak/thrive/tree/master.svg?style=svg)](https://circleci.com/gh/FilipMalczak/thrive/tree/master)
>
> `dev` [![CircleCI](https://circleci.com/gh/FilipMalczak/thrive/tree/dev.svg?style=svg)](https://circleci.com/gh/FilipMalczak/thrive/tree/dev)

Thrive is a highly opinionated infrastructure framework for Spring Boot/Cloud 
based microservices systems.

It started off as part of my private project `knowingly`. That's the reason for missing commits before the initial one.

It provides main components for a system, where you just write a Spring Boot
app, annotate it in build script (only Gradle supported for now) with capabilites
like "swagger", "API" (for REST API) or "WS" (for websockets), run inside docker
compose project (together with aforementioned components) and it automagically
registers with discovery service, exposes its capabilites through gateway service
and is merged into swagger docs that cover all the services in the project.

> TL;DR: You write service, Swagger, Gateway, Admin, etc are handled for you.

Secondary goal of the project is to sanitize commonly used services (like DBMS, queues, etc) for `docker-compose` usage. Ideally, you could just add something like
`mongoCluster secondaryReplicas: 5` to your build.gradle and all the services that
want to use MongoDB would autoconfigure for the cluster that would be automatically
started from generated `docker-compose.yml`.

General idea is that with modern containerization and orchestration tools, you seldom
change hostnames and ports, as they are just container names and default service ports. 

## ToDo

- figure out what to do with YML files in repository root
- create another test service, preferably make it cooperate with existing one with a webhook and/or messaging
- expand tests
- create BOM module for lib version alignment
- add docker image publishing (push to dockerhub) from CI (at `master` and `dev` branches; make sure that test services aren't published)
- publish BOM to some Maven repo (probably JFrog)
- extend `thrive` configurability (ports, enabling/disabling admin and swagger ui, etc), maybe provide `docker-compose-ui.yml`
- extend e2e tests to check configurability (see previous point)
- create Gradle plugin for generating `docker-compose.yml` files with curated image lists


## Test scenarios

1. run 2 services in docker-compose, run their acceptance suites (in parallel or interleaved) against gateway (done)
2. run 1 service in docker-compose, another locally, ditto (done; more variations still needed)
3. (once thrive is more configurable) (1) and (2), but after tweaking thrive config (ports, etc) 
4. (1) and (2), but check merged swaggerfiles