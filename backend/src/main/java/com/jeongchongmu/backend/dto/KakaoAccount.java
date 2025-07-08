package com.jeongchongmu.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAccount {

    @JsonProperty("profile_needs_agreement")
    private boolean profileNeedsAgreement;

    @JsonProperty("profile_nickname_needs_agreement")
    private boolean profileNicknameNeedsAgreement;

    @JsonProperty("profile_image_needs_agreement")
    private boolean profileImageNeedsAgreement;

    private Profile profile;

    @JsonProperty("name_needs_agreement")
    private boolean nameNeedsAgreement;

    private String name;

    @JsonProperty("email_needs_agreement")
    private boolean emailNeedsAgreement;

    @JsonProperty("is_email_valid")
    private boolean isEmailValid;

    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;

    private String email;

    @JsonProperty("age_range_needs_agreement")
    private boolean ageRangeNeedsAgreement;

    @JsonProperty("age_range")
    private String ageRange;

    @JsonProperty("birthyear_needs_agreement")
    private boolean birthYearNeedsAgreement;

    private String birthYear;

    @JsonProperty("birthday_needs_agreement")
    private boolean birthdayNeedsAgreement;

    private String birthday;

    @JsonProperty("birthday_type")
    private String birthdayType;

    @JsonProperty("is_leap_month")
    private boolean isLeapMonth;

    @JsonProperty("gender_needs_agreement")
    private boolean genderNeedsAgreement;

    private String gender;

    @JsonProperty("phone_number_needs_agreement")
    private boolean phoneNumberNeedsAgreement;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("ci_needs_agreement")
    private boolean ciNeedsAgreement;

    private String ci;

    @JsonProperty("ci_authenticated_at")
    private String ciAuthenticatedAt;
}
