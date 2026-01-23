package com.orang3i.skillvault.dto;

import java.util.List;
import java.util.UUID;

public class NodeTreeResponse {
    public UUID id;
    public String title;
    public String category;
    public int mastery;
    public List<NodeTreeResponse> children;
}
