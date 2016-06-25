package com.spedge.hangar.repo.java.healthcheck;

import com.codahale.metrics.health.HealthCheck;

public class JavaRepositoryHealthcheck extends HealthCheck {

	@Override
	protected Result check() throws Exception {
		return Result.healthy();
	}
}
