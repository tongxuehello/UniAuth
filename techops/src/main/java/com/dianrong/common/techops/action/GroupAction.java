package com.dianrong.common.techops.action;

import com.dianrong.common.techops.bean.Node;
import com.dianrong.common.techops.helper.CustomizeBeanConverter;
import com.dianrong.common.uniauth.common.bean.Response;
import com.dianrong.common.uniauth.common.bean.dto.GroupDto;
import com.dianrong.common.uniauth.common.bean.dto.PageDto;
import com.dianrong.common.uniauth.common.bean.dto.RoleDto;
import com.dianrong.common.uniauth.common.bean.request.GroupParam;
import com.dianrong.common.uniauth.common.bean.request.GroupQuery;
import com.dianrong.common.uniauth.common.bean.request.UserListParam;
import com.dianrong.common.uniauth.common.cons.AppConstants;
import com.dianrong.common.uniauth.sharerw.facade.UARWFacade;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Arc on 29/2/2016.
 */
@RestController
@RequestMapping("group")
public class GroupAction {

    @Resource
    private UARWFacade uARWFacade;

    @RequestMapping(value = "/tree" , method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Response<?> getGroupTree(@RequestBody GroupParam groupParam) {
        Response<GroupDto> groupDtoResponse = uARWFacade.getGroupRWResource().getGroupTree(groupParam);
        if(!CollectionUtils.isEmpty(groupDtoResponse.getInfo())) {
            return groupDtoResponse;
        }
        GroupDto groupDto = groupDtoResponse.getData();
        if(groupDto != null) {
            return Response.success(CustomizeBeanConverter.convert(Arrays.asList(groupDto)));
        } else {
            return Response.success(null);
        }
    }

    //need
    @RequestMapping(value = "/add" , method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN') and hasPermission(#groupParam,'PERM_GROUP_OWNER')")
    public Response<GroupDto> addNewGroupIntoGroup(@RequestBody GroupParam groupParam) {
        Response<GroupDto> groupDto = uARWFacade.getGroupRWResource().addNewGroupIntoGroup(groupParam);
        return groupDto;
    }

    @RequestMapping(value = "/get" , method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Response<Node> getGroupDetailsById(@RequestParam("id")Integer grpId) {
        GroupQuery groupQuery = new GroupQuery();
        groupQuery.setId(grpId);
        Response<PageDto<GroupDto>> pageDtoResponse = uARWFacade.getGroupRWResource().queryGroup(groupQuery);
        GroupDto groupDto = pageDtoResponse.getData().getData().get(0);
        Node node = new Node();
        node.setId(groupDto.getId().toString());
        node.setCode(groupDto.getCode());
        node.setType(AppConstants.NODE_TYPE_GROUP);
        node.setLabel(groupDto.getName());
        node.setDescription(groupDto.getDescription());
        return Response.success(node);
    }

    @RequestMapping(value = "/query" , method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public Response<PageDto<GroupDto>> searchGroup(@RequestBody GroupQuery groupQuery) {
        Response<PageDto<GroupDto>> pageDtoResponse = uARWFacade.getGroupRWResource().queryGroup(groupQuery);
        return pageDtoResponse;
    }

    //need
    @RequestMapping(value = "/modify" , method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN') and hasPermission(#groupParam,'PERM_GROUP_OWNER')")
    public Response<GroupDto> modifyGroup(@RequestBody GroupParam groupParam) {
        Response<GroupDto> groupDto = uARWFacade.getGroupRWResource().updateGroup(groupParam);
        return groupDto;
    }

    //need
    @RequestMapping(value = "/adduser" , method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN') and hasPermission(#userListParam,'PERM_GROUP_OWNER')")
    public Response<Void> addUserToGroup(@RequestBody UserListParam userListParam) {
        Response<Void>  response = uARWFacade.getGroupRWResource().addUsersIntoGroup(userListParam);
        return response;
    }
    
    //need
    @RequestMapping(value = "/deleteuser" , method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN') and hasPermission(#userListParam,'PERM_GROUP_OWNER')")
    public Response<Void> removeUserFromGroup(@RequestBody UserListParam userListParam) {
        Response<Void>  response = uARWFacade.getGroupRWResource().removeUsersFromGroup(userListParam);
        return response;
    }

    @RequestMapping(value = "/group-roles" , method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("(hasRole('ROLE_SUPER_ADMIN') and principal.permMap['DOMAIN'] != null and principal.permMap['DOMAIN'].contains('techops')) "
    		+ "or (hasRole('ROLE_ADMIN') and principal.domainIdSet.contains(#groupParam.domainId))")
    public Response<List<RoleDto>>  getAllRolesToGroupAndDomain(@RequestBody GroupParam groupParam) {
        return uARWFacade.getGroupRWResource().getAllRolesToGroupAndDomain(groupParam);
    }

    @RequestMapping(value = "/replace-roles-to-group" , method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("(hasRole('ROLE_SUPER_ADMIN') and principal.permMap['DOMAIN'] != null and principal.permMap['DOMAIN'].contains('techops')) "
    		+ "or (hasRole('ROLE_ADMIN') and principal.domainIdSet.contains(#groupParam.domainId))")
    public Response<Void> replaceRolesToGroup(@RequestBody GroupParam groupParam) {
        return uARWFacade.getGroupRWResource().replaceRolesToGroup(groupParam);
    }
}