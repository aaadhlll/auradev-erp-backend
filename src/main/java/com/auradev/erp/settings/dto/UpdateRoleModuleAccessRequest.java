package com.auradev.erp.settings.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record UpdateRoleModuleAccessRequest(@NotNull Map<String, List<String>> roles) {}
