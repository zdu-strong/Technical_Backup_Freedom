package com.springboot.project.controller;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.baseController.BaseController;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.OrganizeMoveTopModel;

@RestController
public class OrganizeController extends BaseController {

    @PostMapping("/organize/create")
    public ResponseEntity<?> create(@RequestBody OrganizeModel organizeModel) {

        if (organizeModel.getParent() != null) {
            var parentOrganizeId = organizeModel.getParent().getId();
            if (StringUtils.isNotBlank(parentOrganizeId)) {
                this.organizeService.checkExistOrganize(parentOrganizeId);
            }
        }

        var organize = this.organizeService.create(organizeModel);
        return ResponseEntity.ok(organize);
    }

    @PutMapping("/organize/update")
    public ResponseEntity<?> update(@RequestBody OrganizeModel organizeModel) {
        this.organizeService.checkExistOrganize(organizeModel.getId());

        this.organizeService.update(organizeModel);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/organize/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        this.organizeService.checkExistOrganize(id);

        this.organizeService.delete(id);
        this.organizeUtil.refresh(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/organize/move")
    public ResponseEntity<?> move(@RequestParam String id, @RequestParam(required = false) String parentId)
            throws InterruptedException {
        this.organizeService.checkExistOrganize(id);
        if (StringUtils.isNotBlank(parentId)) {
            this.organizeService.checkExistOrganize(parentId);
        }
        this.organizeService.checkOrganizeCanBeMove(id, parentId);

        OrganizeMoveTopModel[] organizeMoveTopList;
        var initStartDate = new Date();
        while (true) {
            try {
                organizeMoveTopList = this.organizeMoveTopService.createOrganizeMoveTop(id, parentId);
                break;
            } catch (DataIntegrityViolationException e) {
                if (!initStartDate.before(DateUtils.addSeconds(new Date(), -5))) {
                    Thread.sleep(1);
                    continue;
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Too many requests to move the organize, please wait a minute and try again");
            }
        }
        try {
            this.organizeService.move(id, parentId);
        } finally {
            this.organizeMoveTopService.deleteOrganizeMoveTop(organizeMoveTopList);
        }

        this.organizeUtil.refresh(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organize")
    public ResponseEntity<?> getOrganizeById(@RequestParam String id) {

        this.organizeService.checkExistOrganize(id);

        var organizeModel = this.organizeService.getById(id);
        return ResponseEntity.ok(organizeModel);
    }

}
