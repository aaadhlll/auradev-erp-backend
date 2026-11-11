package com.auradev.erp.settings.dto;

import java.util.List;
import java.util.Map;

public record RoleModuleAccessResponse(Map<String, List<String>> roles) {}
