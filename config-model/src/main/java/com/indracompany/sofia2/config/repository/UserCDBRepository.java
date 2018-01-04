/*******************************************************************************

 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.indracompany.sofia2.config.model.UserCDB;

public interface UserCDBRepository extends JpaRepository<UserCDB, String> {

          
    List<UserCDB> findByEmail(String email);
    List<UserCDB> findByUserId(String userId);
    
    @Query("SELECT o FROM UserCDB AS o WHERE o.role !='1'")
    List<UserCDB> findUsersNoAdmin();
}
