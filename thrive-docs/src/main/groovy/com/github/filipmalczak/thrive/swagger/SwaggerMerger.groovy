package com.github.filipmalczak.thrive.swagger

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class SwaggerMerger {
    String getMergedDocs(String baseUrl, List<String> urls){
        def slurper = new JsonSlurper()
        def json = slurper.parse(this.class.getResourceAsStream("/swagger-about.json"))
        json.host = baseUrl
        json.tags = []
        json.paths = [:]
        json.info.description = this.class.getResource("/swagger-description.md").text
        //todo missing schemes (http(s)), securityDefinitions
        //todo document websockets as externalDocs
        json.definitions = [:]
        urls.each { url ->
            def fullUrl = url+"/v2/api-docs?group=api"
            log.info("Gonna parse $fullUrl")
            slurper.parse(new URL(fullUrl)).with { parsed ->
                if (parsed.tags)
                    json.tags += parsed.tags //todo make them unique
                (parsed.paths as Map).each { path, methodToDesc ->
                    if ((json.paths as Map).containsKey(path)) {
                        (methodToDesc as Map).each { method, desc ->
                            json.paths[path][method] = desc
                        }
                    } else {
                        json.paths[path] = methodToDesc
                    }
                }
                json.definitions.putAll(parsed.definitions ?: [:])
            }
        }
        return JsonOutput.toJson(json)
    }
}