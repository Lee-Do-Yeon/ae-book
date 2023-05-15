package com.c201.aebook.api.user.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.c201.aebook.api.common.BaseEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user")
public class UserEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "kakao_id", length = 200)
	private Long kakaoId;

	@Column(name = "nickname", length = 100)
	private String nickname;

	@Column(name = "phone", length = 100)
	private String phone;

	@Column(name = "profile_url", length = 200)
	// @Column(name = "profile_url", length = 200, columnDefinition = "카카오에서 주는 url")
	private String profileUrl;

	@Column(name = "status", nullable = false, columnDefinition = "TINYINT default 1")
	private int status;

	@Builder
	public UserEntity(Long id, Long kakaoId, String nickname, String phone, String profileUrl, int status) {
		this.id = id;
		this.kakaoId = kakaoId;
		this.nickname = nickname;
		this.phone = phone;
		this.profileUrl = profileUrl;
		this.status = status;
	}

	public void updateUserEntity(String nickname, String profileUrl) {
		this.nickname = nickname;
		this.profileUrl = profileUrl;
	}

	public void invalidateUserEntity(Long kakaoId, String phone, String nickname, int status, String profileUrl) {
		this.kakaoId = kakaoId;
		this.phone = phone;
		this.nickname = nickname;
		this.status = status;
		this.profileUrl = profileUrl;
	}

}
