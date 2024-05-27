package com.hu.oneclick.controller.req;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterBody {
  /**
   * 邮箱
   */
  @NotBlank(message = "邮箱不能为空")
  private String email;
  /**
   * 用户名
   */
  private String userName;
  /**
   * 联系方式
   */
  private String contactNo;
  /**
   * 用户所在企业
   */
  private String company;
  /**
   * 职业
   */
  private String profession;
  /**
   * 行业
   */
  private String industry;
}
