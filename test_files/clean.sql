database tcs_dw;

DELETE FROM streak_type_lu;
DELETE FROM calendar;

database tcs_catalog;

DELETE FROM project_platform WHERE project_id >= 40000000;
DELETE FROM project_file_type_xref WHERE project_id >= 40000000;
DELETE FROM comp_technology WHERE comp_tech_id >= 40000000;
DELETE FROM informixoltp:payment WHERE payment_id >= 40000000;
DELETE FROM informixoltp:payment_detail WHERE payment_detail_id >= 40000000;
DELETE FROM contest_sale WHERE contest_sale_id >= 40000000;
DELETE FROM notification WHERE project_id >= 40000000;
DELETE FROM review_item_comment WHERE review_item_comment_id >= 40000000;
DELETE FROM review_item WHERE review_item_id >= 40000000;
DELETE FROM review_comment WHERE review_comment_id >= 40000000;
DELETE FROM review WHERE review_id >= 40000000;
DELETE FROM screening_task WHERE screening_task_id >= 40000000;
DELETE FROM resource_submission WHERE resource_id >= 40000000;
DELETE FROM resource_info WHERE resource_id >= 40000000;
DELETE FROM project_result WHERE project_id >= 40000000;
DELETE FROM component_inquiry WHERE component_id >= 40000000;
DELETE FROM phase_criteria WHERE project_phase_id >= 40000000;
DELETE FROM phase_dependency WHERE dependency_phase_id >= 40000000;
DELETE FROM project_spec WHERE project_spec_id >= 40000000;
DELETE FROM project_info WHERE project_id >= 40000000;
DELETE FROM comp_version_dates;
DELETE FROM comp_versions WHERE comp_vers_id >= 40000000;
DELETE FROM comp_categories WHERE comp_categories_id >= 40000000;
DELETE FROM comp_catalog WHERE component_id >= 40000000;
DELETE FROM copilot_project WHERE copilot_project_id >= 40000000;
DELETE FROM copilot_profile WHERE copilot_profile_id >= 40000000;
DELETE FROM informixoltp:coder WHERE coder_id >= 40000000;
DELETE FROM common_oltp:corona_event WHERE user_id >= 40000000;
DELETE FROM common_oltp:email WHERE email_id >= 40000000;
DELETE FROM common_oltp:user_security_key WHERE user_id >= 40000000;
DELETE FROM common_oltp:security_user WHERE login_id >= 40000000;
DELETE FROM common_oltp:user WHERE user_id >= 40000000;
DELETE FROM rboard_user;
DELETE FROM user_rating WHERE user_id >= 40000000;
DELETE FROM user_terms_of_use_xref WHERE user_id >= 40000000;

UPDATE project SET project_studio_spec_id = NULL;
DELETE FROM project_studio_specification WHERE project_studio_spec_id >= 40000000;
DELETE FROM project_payment;
DELETE FROM submission WHERE upload_id >= 40000000;
DELETE FROM upload WHERE project_id >= 40000000;
DELETE FROM resource WHERE project_id >= 40000000;
DELETE FROM project_phase_audit;
DELETE FROM project_phase WHERE project_phase_id >= 40000000;
DELETE FROM prize WHERE prize_id >= 40000000;
DELETE FROM project_audit WHERE project_id >= 40000000;
DELETE FROM project_info_audit WHERE project_id >= 40000000;
DELETE FROM project WHERE project_id >= 40000000;


database corporate_oltp;

DELETE FROM tc_direct_project WHERE project_id >= 40000000;
DELETE FROM user_permission_grant WHERE user_permission_grant_id >= 40000000;

DATABASE time_oltp;

DELETE FROM project_worker WHERE project_id >= 40000000;
DELETE FROM project_manager WHERE project_id >= 40000000;
DELETE FROM user_account WHERE user_account_id >= 40000000;
DELETE FROM client_project WHERE client_id >= 40000000;
DELETE FROM project WHERE project_id >= 40000000;
DELETE FROM client WHERE client_id >= 40000000;
DELETE FROM payment_terms WHERE payment_terms_id >= 40000000;
DELETE FROM company WHERE company_id >= 40000000;
