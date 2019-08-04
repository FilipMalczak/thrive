# Thrive

> CircleCI status:
>
> `master` [![CircleCI](https://circleci.com/gh/FilipMalczak/thrive/tree/master.svg?style=svg)](https://circleci.com/gh/FilipMalczak/thrive/tree/master)
>
> `dev` [![CircleCI](https://circleci.com/gh/FilipMalczak/thrive/tree/dev.svg?style=svg)](https://circleci.com/gh/FilipMalczak/thrive/tree/dev)

**Thrive is a highly opinionated infrastructure framework for Spring Boot/Cloud 
based microservices systems.**

It started off as part of my private project `knowingly`. That's the reason for missing commits before the initial one.
In turn, `knowingly` is a conceptual prodigy of my other project - [`vent`](https://github.com/filipmalczak/vent)
that became a leaking abstraction of DB over data sourcing, but the original idea was that we need to know history
of our systems, not only the status quo.

Thrive provides main components for a system, where you just write a Spring Boot
app, annotate it in build script (only Gradle supported for now) with capabilites
like "swagger", "API" (for REST API) or "WS" (for websockets), run inside docker
compose project (together with aforementioned components) and it automagically
registers with discovery service, exposes its capabilites through gateway service
and is merged into swagger docs that cover all the services in the project.

> TL;DR: You write service, Swagger, Gateway, Admin, etc are handled for you.

Secondary goal of the project is to sanitize commonly used services (like DBMS, queues, etc) for `docker-compose` usage. 
Ideally, you could just add something like
`mongoCluster secondaryReplicas: 5` to your build.gradle and all the services that
want to use MongoDB would autoconfigure for the cluster that would be automatically
started from generated `docker-compose.yml`.

General idea is that with modern containerization and orchestration tools, you seldom
change hostnames and ports, as they are just container names and default service ports. 

## Main features

> All of these are already there, in one way or the other. Sometimes you'll need to copy `docker-compose.yml` file
> contents, sometimes you'll need to describe capabilities of your service in your `build.gradle`, but with time
> you'll just apply the Gradle plugin and write the business code.
>
> Basically, stay tuned, these features will get more automated, but this is my pet project and I need to earn my
> living in the meantime.

* sanitized general purpose services
  * you get DBs like MongoDB and PostgreSQL as well as messaging (Kafka-based) out-of-the-box, no manual config needed
* preconfigured infrastructure 
  * you get Spring Boot Admin, SwaggerUI (see below) and more in the future for free 
* autodiscovery and autoexposure
  * you write the service, put it in docker-compose and it is discovered and registered with discovery service, as well
as with gateway
* autodocumentation
  * your public API (endpoints starting with `/api`) are automagically visible through gateway; your internal APIs (where 
target audience is other services inside compose project, starting with `/internal`) and technical APIs (at this point - 
everything that is not public or internal API, usually Actuator endpoints) are scoped in different swaggerfiles; public 
API is merged into single swaggerfile, exposed via gateway, and there is SwaggerUI available too
  > To be completely honest, I'm not sure how to approach internal and technical APIs yet. For now, only public API is 
  > exposed with SwaggerUI.

## Involvement

Do you want to help?

I really hope that your answer is "yes".  If so, get in touch (via [github issues](https://github.com/FilipMalczak/thrive/issues),
or via email - `filip(dot)malczak(at)gmail(dot)com`).

I need help in implementation (duh), but I'm also looking for new ideas and feature requests. I could use help from 
developers, testers (seriously, testing a framework is hella tricky, I need someone who thinks like a tester, not a dev),
but also architects (I'm counting on your feature requests, I have my ideas, but I'm mainly dev-oriented in the end).

> If you're just gonna submit a pull request, it'd make my day. I'll probably wanna talk to you, but I'll appreciate
> the hell of that. 

I'm not gonna try and come up with convention for issue tagging - there's no point unless there's gonna be a mess in 
issue section.

## ToDo

- create BOM module for lib version alignment
- add docker image publishing (push to dockerhub) from CI (at `master` and `dev` branches; make sure that test services aren't published)
- publish BOM to some Maven repo (probably JFrog)
- create Gradle plugin for generating `docker-compose.yml` files with curated image lists
- add support for scaling (many instances of the same service should be reouted with Ribbon in gateway and appear only once in Swagger)
- add support for preconfigured MongoDB
- add support for preconfigured PostgreSQL

### Low prio

- figure out what to do with YML files in repository root
- add support for internal/technical APIs
- expand tests
- extend `thrive` configurability (ports, enabling/disabling admin and swagger ui, etc), maybe provide `docker-compose-ui.yml`
- extend e2e tests to check configurability (see previous point)

### Far future

- support for other languages (D2, Python, node.js, in that order; .NET Core is a strong possibility)

## Test scenarios

1. run 2 services in docker-compose, run their acceptance suites (in parallel or interleaved) against gateway (done)
2. run 1 service in docker-compose, another locally, ditto (done; more variations still needed)
3. (once thrive is more configurable) (1) and (2), but after tweaking thrive config (ports, etc) 
4. (1) and (2), but check merged swaggerfiles