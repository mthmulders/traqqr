CREATE
    TABLE
        job_item(
            id uuid PRIMARY KEY NOT NULL,
            job_type VARCHAR NOT NULL,
            instance_id BIGINT NOT NULL,
            execution_id BIGINT NOT NULL,
            item_status VARCHAR NOT NULL,
            item_id UUID NOT NULL
        );

-- Every item can be processed only once per job execution.
ALTER TABLE
    job_item ADD CONSTRAINT single_processing_per_job_execution UNIQUE(
        instance_id,
        execution_id,
        item_id
    );