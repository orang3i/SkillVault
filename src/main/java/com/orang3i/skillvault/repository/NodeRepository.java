package com.orang3i.skillvault.repository;

import com.orang3i.skillvault.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<Node, UUID> {

    List<Node> findByParentId(UUID parentId);

    // For roots (parent is null)
    List<Node> findByParentIsNull();

    Optional<Node> findById(UUID id);
}
