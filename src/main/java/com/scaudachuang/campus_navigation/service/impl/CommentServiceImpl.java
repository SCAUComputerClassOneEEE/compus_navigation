package com.scaudachuang.campus_navigation.service.impl;

import com.scaudachuang.campus_navigation.DAO.CommentDAO;
import com.scaudachuang.campus_navigation.entity.Comment;
import com.scaudachuang.campus_navigation.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    CommentDAO commentDAO;

    @Override
    public Page<Comment> findByPage(int page, int size, int bId) {
        return commentDAO.findAll((Specification<Comment>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicatesList = new ArrayList<>();
            //动态sql
            predicatesList.add(criteriaBuilder.equal(root.get("bid"),bId));
            Predicate[] p = new Predicate[predicatesList.size()];
            return criteriaBuilder.and(predicatesList.toArray(p));

        }, PageRequest.of(page,size, Sort.Direction.DESC,"timeOfCommentary"));
    }

    @Override
    public List<Comment> findByUid(int uId) {
        return commentDAO.findAllByUid(uId);
    }

    public void save(Comment comment){
        commentDAO.save(comment);
    }
}
