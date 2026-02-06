package com.orang3i.skillvault.controller;


import com.orang3i.skillvault.dto.*;
import com.orang3i.skillvault.service.NodeService;
import jakarta.validation.Valid;
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
    public NodeResponse create(@Valid @RequestBody NodeCreateRequest req) {
        return nodeService.create(req);
    }

    @GetMapping("/{id}")
    public NodeResponse get(@PathVariable UUID id) {
        return nodeService.get(id);
    }

    @PutMapping("/{id}")
    public NodeResponse update(@PathVariable UUID id, @Valid @RequestBody NodeUpdateRequest req) {
        return nodeService.update(id, req);
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
