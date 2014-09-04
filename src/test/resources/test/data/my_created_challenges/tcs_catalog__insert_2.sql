update resource set user_id = (select value from resource_info where resource.resource_id=resource_info.resource_id and resource_info_type_id=1) where user_id is null;
update project_phase set actual_end_time = "2014-08-20 20:00:00";
update project_phase set actual_start_time = "2014-08-1 20:00:00";