workspace polly {
    !identifiers hierarchical

    model {
        user = person "User" "Someone who drives an electric car"
        car = element "Car" "An electric vehicle"
        adapter = element "Adapter" "One or more hardware and/or software adapters"
        traqqr = softwareSystem "Traqqr" "An application for calculating electric car energy usage" {
            database = container "PostgreSQL" "PostgreSQL database" {
                tags "Database"
            }
            application = container "Traqqr" "Traqqr application" {
                domain = component "Domain"
                
                batch = component "Batch processing" {
                    tags "Adapter"
                }
                web = component "Web application" {
                    tags "Adapter"
                }
                api = component "REST API" {
                    tags "Adapter"
                }

                rdbms-persistence = component "Relational database persistence" {
                    tags "Adapter"
                }
                liberty-security-wrapper = component "OpenLiberty wrapper" {
                    tags "Adapter"
                }

                web -> domain "Uses API"
                api -> domain "Uses API"
                batch -> domain "Uses API"

                liberty-security-wrapper -> domain "Implements SPI"
                rdbms-persistence -> domain "Implements SPI"
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