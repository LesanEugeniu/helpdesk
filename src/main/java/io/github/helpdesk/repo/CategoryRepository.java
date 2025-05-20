package io.github.helpdesk.repo;

import io.github.helpdesk.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Comment, Long> {



}
