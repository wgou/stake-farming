package io.renren.modules.sys.form;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.UpdateGroup;
import lombok.Data;
@Data
public class SysUserFrom {

	  /**
   * 用户ID
   */
  @TableId
  private Long userId;

  /**
   * 用户名
   */
  @NotBlank(message = "用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private String username;
  
  private String password;

  /**
   * 邮箱
   */
  private String email;

 
  /**
   * 状态  0：禁用   1：正常
   */
  private Integer status;

  /**
   * 角色ID列表
   */
  @TableField(exist = false)
  private List<Long> roleIdList;
  
  
  private String googleAuthCode;
 

  

}
