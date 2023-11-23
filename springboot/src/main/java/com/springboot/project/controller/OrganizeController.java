package com.springboot.project.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.project.model.OrganizeModel;

@RestController
public class OrganizeController extends BaseController {

    @PostMapping("/organize")
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

    @PutMapping("/organize")
    public ResponseEntity<?> update(@RequestBody OrganizeModel organizeModel) {
        this.organizeService.checkExistOrganize(organizeModel.getId());

        this.organizeService.update(organizeModel);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/organize")
    public ResponseEntity<?> deleteOrganize(@RequestParam String id) {

        this.organizeService.checkExistOrganize(id);

        this.organizeService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organize")
    public ResponseEntity<?> getOrganizeById(@RequestParam String id) {

        this.organizeService.checkExistOrganize(id);

        var organizeModel = this.organizeService.getById(id);
        return ResponseEntity.ok(organizeModel);
    }

}
