package com.orang3i.skillvault.dto;

import java.time.Instant;
import java.util.UUID;

public class NodeResponse {
    public UUID id;
    public String title;
    public String description;
    public String category;
    public int mastery;
    public UUID parentId;
    public Instant createdAt;
    public Instant updatedAt;
}
