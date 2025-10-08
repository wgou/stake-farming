package io.renren.modules.core.param;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class WithDrawParam extends PageParam{

	private String wallet;
	
	private Integer stake; //是否抵押 0：未抵押 1：已抵押
	
	private Long inviteId; //招聘员ID
	
	private Integer real; //是否真实 0：真实 1:虚拟
	
	private Integer status; //状态 0：待处理  1：处理中 2：成功  -1： 失败
	
	private Integer hash; //是否又Hash 0:没有 1：有
	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date start;
	 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
	private Date end;
}
