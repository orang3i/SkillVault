package com.orang3i.skillvault.controller;


import com.orang3i.skillvault.dto.NodeCreateRequest;
import com.orang3i.skillvault.dto.NodeMoveRequest;
import com.orang3i.skillvault.dto.NodeResponse;
import com.orang3i.skillvault.dto.NodeTreeResponse;
import com.orang3i.skillvault.service.NodeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {
    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @PostMapping
    public NodeResponse create(@RequestBody NodeCreateRequest req) {
        return nodeService.create(req);
    }

    @GetMapping("/{id}")
    public NodeResponse get(@PathVariable UUID id) {
        return nodeService.get(id);
    }

    @GetMapping
    public List<NodeResponse> list(@RequestParam(required = false) UUID parentId) {
        return nodeService.listChildren(parentId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        nodeService.delete(id);
    }

    @GetMapping("/{id}/subtree")
    public NodeTreeResponse subtree(@PathVariable UUID id) {
        return nodeService.getSubtree(id);
    }

    @GetMapping("/tree")
    public List<NodeTreeResponse> fullTree() {
        return nodeService.getFullTree();
    }

    @PatchMapping("/{id}/move")
    public NodeResponse move(@PathVariable java.util.UUID id, @RequestBody NodeMoveRequest req) {
        return nodeService.move(id, req.newParentId);
    }

}
