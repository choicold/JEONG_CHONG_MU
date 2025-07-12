package com.jeongchongmu.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAccount(
        @JsonProperty("profile_needs_agreement")
        boolean profileNeedsAgreement,

        @JsonProperty("profile_nickname_needs_agreement")
        boolean profileNicknameNeedsAgreement,

        @JsonProperty("profile_image_needs_agreement")
        boolean profileImageNeedsAgreement,

        Profile profile,

        @JsonProperty("name_needs_agreement")
        boolean nameNeedsAgreement,

        String name,

        @JsonProperty("email_needs_agreement")
        boolean emailNeedsAgreement,

        @JsonProperty("is_email_valid")
        boolean isEmailValid,

        @JsonProperty("is_email_verified")
        boolean isEmailVerified,

        String email,

        @JsonProperty("age_range_needs_agreement")
        boolean ageRangeNeedsAgreement,

        @JsonProperty("age_range")
        String ageRange,

        @JsonProperty("birthyear_needs_agreement")
        boolean birthYearNeedsAgreement,

        String birthYear,

        @JsonProperty("birthday_needs_agreement")
        boolean birthdayNeedsAgreement,

        String birthday,

        @JsonProperty("birthday_type")
        String birthdayType,

        @JsonProperty("is_leap_month")
        boolean isLeapMonth,

        @JsonProperty("gender_needs_agreement")
        boolean genderNeedsAgreement,

        String gender,

        @JsonProperty("phone_number_needs_agreement")
        boolean phoneNumberNeedsAgreement,

        @JsonProperty("phone_number")
        String phoneNumber,

        @JsonProperty("ci_needs_agreement")
        boolean ciNeedsAgreement,

        String ci,

        @JsonProperty("ci_authenticated_at")
        String ciAuthenticatedAt
) {
}