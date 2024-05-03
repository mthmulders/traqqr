workspace polly {
    !identifiers hierarchical

    model {
        user = person "User" "Someone who drives an electric car"
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
    }
    views {
        systemContext "Traqqr" "traqqr-context" {
            include user traqqr
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