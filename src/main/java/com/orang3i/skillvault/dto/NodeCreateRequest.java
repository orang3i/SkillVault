package com.orang3i.skillvault.dto;

import java.util.UUID;

public class NodeCreateRequest {
    public String title;
    public String description;
    public String category;
    public int mastery;
    public UUID parentId;
}
