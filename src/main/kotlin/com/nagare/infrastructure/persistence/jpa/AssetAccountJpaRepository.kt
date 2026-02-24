package com.nagare.infrastructure.persistence.jpa

import com.nagare.infrastructure.persistence.jpa.entity.AssetAccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AssetAccountJpaRepository : JpaRepository<AssetAccountEntity, String>
