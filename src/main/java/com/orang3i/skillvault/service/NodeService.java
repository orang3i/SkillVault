package com.orang3i.skillvault.service;

import com.orang3i.skillvault.dto.NodeCreateRequest;
import com.orang3i.skillvault.dto.NodeResponse;
import com.orang3i.skillvault.dto.NodeTreeResponse;
import com.orang3i.skillvault.dto.NodeUpdateRequest;
import com.orang3i.skillvault.entity.Node;
import com.orang3i.skillvault.exception.NotFoundException;
import com.orang3i.skillvault.repository.NodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;

    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Transactional
    public NodeResponse create(NodeCreateRequest req) {
        Node parent = null; //if node is parent node then parent of that node remains null
        if (req.parentId != null) {
            parent = nodeRepository.findById(req.parentId).orElseThrow(() -> new NotFoundException("parent not found"));
        }

        Node node = new Node();
        node.setTitle(req.title);
        node.setDescription(req.description);
        node.setCategory(req.category);
        node.setMastery(req.mastery);
        node.setParent(parent);

        Node saved = nodeRepository.save(node);

        return toResponse(saved);
    }

    @Transactional
    public NodeResponse update(UUID id, NodeUpdateRequest req) {
        Node node = nodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Node not found"));

        // Update fields
        node.setTitle(req.title);
        node.setDescription(req.description);
        node.setCategory(req.category);
        node.setMastery(req.mastery);

        Node saved = nodeRepository.save(node);
        return toResponse(saved);
    }

    @Transactional()
    public NodeResponse get(UUID id) {
        Node node = nodeRepository.findById(id).orElseThrow(() -> new NotFoundException("node not found"));
        return toResponse(node);
    }

    @Transactional()
    public List<NodeResponse> listChildren(UUID parentId) {
        List<Node> nodes = (parentId == null) ? nodeRepository.findByParentIsNull() : nodeRepository.findByParentId(parentId);

        return nodes.stream().map(this::toResponse).toList();
    }

    @Transactional
    public void delete(UUID id) {
        if (!nodeRepository.existsById(id)) {
            throw new NotFoundException("node not found");
        }
        nodeRepository.deleteById(id);
    }

    @Transactional
    public NodeTreeResponse getSubtree(UUID id) {
        Node root = nodeRepository.findById(id).orElseThrow(() -> new NotFoundException("node not found"));
        return buildTree(root);
    }

    private NodeTreeResponse buildTree(Node node) {
        NodeTreeResponse r = new NodeTreeResponse();
        r.id = node.getId();
        r.title = node.getTitle();
        r.category = node.getCategory();
        r.mastery = node.getMastery();

        r.children = node.getChildren().stream().map(this::buildTree).toList();

        return r;
    }

    @Transactional
    public List<NodeTreeResponse> getFullTree() {
        return nodeRepository.findByParentIsNull().stream().map(this::buildTree).toList();
    }

    @jakarta.transaction.Transactional
    public NodeResponse move(UUID nodeId, UUID newParentId) {
        Node node = nodeRepository.findById(nodeId).orElseThrow(() -> new NotFoundException("node not found"));

        Node newParent = null;
        if (newParentId != null) {
            newParent = nodeRepository.findById(newParentId).orElseThrow(() -> new NotFoundException("new parent not found"));
        }

        // can't move under itself
        if (newParent != null && node.getId().equals(newParent.getId())) {
            throw new IllegalArgumentException("cannot move a node under itself");
        }

        // cycle prevention: can't move node under its descendant
        if (newParent != null && isAncestor(node, newParent)) {
            throw new IllegalArgumentException("cannot move a node under its descendant (cycle)");
        }

        node.setParent(newParent);
        Node saved = nodeRepository.save(node);
        return toResponse(saved);
    }

    /**
     * Returns true if "candidate" is somewhere under "node" in the tree.
     * We check by walking UP from candidate -> parent -> parent ... until null,
     * and see if we hit node.
     */
    private boolean isAncestor(Node node, Node candidate) {
        Node cur = candidate;
        while (cur != null) {
            if (cur.getId().equals(node.getId())) return true;
            cur = cur.getParent();
        }
        return false;
    }


    private NodeResponse toResponse(Node node) {
        NodeResponse r = new NodeResponse();
        r.id = node.getId();
        r.title = node.getTitle();
        r.description = node.getDescription();
        r.category = node.getCategory();
        r.mastery = node.getMastery();
        r.parentId = (node.getParent() == null) ? null : node.getParent().getId();
        r.createdAt = node.getCreatedAt();
        r.updatedAt = node.getUpdatedAt();
        return r;
    }
}
