package com.orang3i.skillvault.dto;

import java.util.UUID;

public class NodeMoveRequest {
    public UUID newParentId; // null => move to root
}
