package com.example.shoppingmall.global.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private Boolean deleted = false;

	private LocalDateTime deletedAt;

	public void softDelete() {
		this.deleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	public boolean isDeleted() {
		return deleted;
	}

}
