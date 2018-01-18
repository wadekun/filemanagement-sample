package com.liangck.filemanagement.sample.repository;

import com.liangck.filemanagement.sample.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author liangck
 * @version 1.0
 * @since 2018/1/16 18:08
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long>{

}
