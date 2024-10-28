workspace polly {
    !identifiers hierarchical

    model {
        user = person "User" "Someone who drives an electric car"
        car = element "Car" "An electric vehicle"
        adapter = element "Adapter" "One or more hardware and/or software adapters"
        traqqr = softwareSystem "Traqqr" "A web application for calculating electric car energy usage" {
            database = container "PostgreSQL" "PostgreSQL database" {
                tags "Database"
            }
            application = container "Traqqr" "Traqqr application" {
                domain = component "Domain"
                application = component "Application services"

                web = component "Web application" {
                    tags "Port"
                }
                api = component "REST API" {
                    tags "Port"
                }

                rdbms-persistence = component "Relational database persistence" {
                    tags "Adapter"
                }

                web -> domain "Interacts with"
                api -> domain "Interacts with"

                rdbms-persistence -> domain "Persists"
            }
            application -> database "Uses"
        }

        user -> Traqqr "(Re)view trips, view energy consumption"
        car -> adapter "Extract measurements from the vehicle"
        adapter -> Traqqr "Submit measurements to Traqqr's HTTP API"
    }
    views {
        systemContext "Traqqr" "traqqr-context" {
            include user traqqr car adapter
            autoLayout
        }
        container traqqr "traqqr-container" {
            include *
            autoLayout lr
        }
        component traqqr.application "traqqr-application-component" {
            include * 
            autoLayout lr
        }

        styles {
            element "Database" {
                shape Cylinder
            }
        }
        
        theme default
    }
}