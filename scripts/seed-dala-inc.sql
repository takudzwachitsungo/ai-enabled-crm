begin;

update identitytenancy_tenantprofile
set name = 'Dala Inc'
where tenant_id = 'dala-inc';

delete from audit_logs where tenant_id = 'dala-inc';
delete from aiassistant_aiinteraction where tenant_id = 'dala-inc';
delete from reporting_reportsnapshot where tenant_id = 'dala-inc';
delete from communication_conversationrecord where tenant_id = 'dala-inc';
delete from crm_activities where tenant_id = 'dala-inc';
delete from service_ticket where tenant_id = 'dala-inc';
delete from service_sla_policy where tenant_id = 'dala-inc';
delete from commerce_invoice where tenant_id = 'dala-inc';
delete from commerce_quote where tenant_id = 'dala-inc';
delete from commerce_product where tenant_id = 'dala-inc';
delete from marketing_campaign where tenant_id = 'dala-inc';
delete from marketing_audience_segment where tenant_id = 'dala-inc';
delete from service_canned_response where tenant_id = 'dala-inc';
delete from service_knowledge_base_article where tenant_id = 'dala-inc';
delete from workflow_workflowdefinition where tenant_id = 'dala-inc';
delete from platform_customentityrecord where tenant_id = 'dala-inc';
delete from platform_customentitydefinition where tenant_id = 'dala-inc';
delete from integration_integrationconnection where tenant_id = 'dala-inc';
delete from crm_opportunities where tenant_id = 'dala-inc';
delete from crm_contacts where tenant_id = 'dala-inc';
delete from crm_leads where tenant_id = 'dala-inc';
delete from crm_accounts where tenant_id = 'dala-inc';

insert into crm_accounts (tenant_id, name, industry, website, created_at) values
('dala-inc', 'ZimEdu Group', 'Education', 'https://zimedu.example.com', '2026-03-03T08:15:00Z'),
('dala-inc', 'Harare Retail Holdings', 'Retail', 'https://harareretail.example.com', '2026-03-05T09:30:00Z'),
('dala-inc', 'Mosi Logistics', 'Logistics', 'https://mosilogistics.example.com', '2026-03-08T11:10:00Z'),
('dala-inc', 'Savanna Health Network', 'Healthcare', 'https://savannahealth.example.com', '2026-03-10T10:20:00Z'),
('dala-inc', 'Copperline Manufacturing', 'Manufacturing', 'https://copperline.example.com', '2026-03-13T13:45:00Z');

insert into crm_leads (tenant_id, full_name, email, status, created_at) values
('dala-inc', 'Nomsa Moyo', 'nomsa.moyo@zimedu.example.com', 'NEW', '2026-03-12T08:20:00Z'),
('dala-inc', 'Tatenda Ncube', 'tatenda.ncube@harareretail.example.com', 'CONTACTED', '2026-03-13T09:05:00Z'),
('dala-inc', 'Ashley Zhou', 'ashley.zhou@mosilogistics.example.com', 'QUALIFIED', '2026-03-14T10:40:00Z'),
('dala-inc', 'Kundai Sibanda', 'kundai.sibanda@savannahealth.example.com', 'NURTURE', '2026-03-16T12:10:00Z'),
('dala-inc', 'Brian Chuma', 'brian.chuma@copperline.example.com', 'QUALIFIED', '2026-03-18T08:55:00Z'),
('dala-inc', 'Rumbidzai Dube', 'r.dube@greenfieldsenergy.example.com', 'NEW', '2026-03-20T15:25:00Z');

insert into crm_contacts (tenant_id, full_name, email, company_name, created_at) values
('dala-inc', 'Nomsa Moyo', 'nomsa.moyo@zimedu.example.com', 'ZimEdu Group', '2026-03-12T08:30:00Z'),
('dala-inc', 'Tatenda Ncube', 'tatenda.ncube@harareretail.example.com', 'Harare Retail Holdings', '2026-03-13T09:15:00Z'),
('dala-inc', 'Ashley Zhou', 'ashley.zhou@mosilogistics.example.com', 'Mosi Logistics', '2026-03-14T10:50:00Z'),
('dala-inc', 'Kundai Sibanda', 'kundai.sibanda@savannahealth.example.com', 'Savanna Health Network', '2026-03-16T12:20:00Z'),
('dala-inc', 'Brian Chuma', 'brian.chuma@copperline.example.com', 'Copperline Manufacturing', '2026-03-18T09:05:00Z');

insert into crm_opportunities (tenant_id, name, account_name, amount, stage, created_at) values
('dala-inc', 'ZimEdu Admissions Rollout', 'ZimEdu Group', 245000.00, 'QUALIFIED', '2026-03-06T09:00:00Z'),
('dala-inc', 'Retail Loyalty Automation', 'Harare Retail Holdings', 162500.00, 'PROPOSAL', '2026-03-09T10:00:00Z'),
('dala-inc', 'Fleet Visibility Modernization', 'Mosi Logistics', 98000.00, 'NEGOTIATION', '2026-03-11T11:15:00Z'),
('dala-inc', 'Patient Journey Workspace', 'Savanna Health Network', 187000.00, 'NEW', '2026-03-15T12:00:00Z'),
('dala-inc', 'Factory Service Desk Expansion', 'Copperline Manufacturing', 128000.00, 'PROPOSAL', '2026-03-17T14:10:00Z');

insert into service_sla_policy (tenant_id, name, priority, response_hours, default_assignee, active, created_at) values
('dala-inc', 'Priority Critical', 'HIGH', 4, 'Support Lead', true, '2026-03-03T07:00:00Z'),
('dala-inc', 'Priority Standard', 'MEDIUM', 12, 'Customer Success Desk', true, '2026-03-03T07:05:00Z'),
('dala-inc', 'Priority Routine', 'LOW', 24, 'Service Operations', true, '2026-03-03T07:10:00Z');

insert into service_ticket (
    tenant_id, title, description, priority, status, assignee, source_channel,
    related_entity_type, related_entity_id, due_at, escalated_at, resolved_at, created_at
) values
(
    'dala-inc',
    'Admissions portal handoff',
    'Coordinate the admissions rollout handoff for the ZimEdu launch.',
    'HIGH',
    'IN_PROGRESS',
    'Support Lead',
    'EMAIL',
    'ACCOUNT',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'ZimEdu Group'),
    '2026-03-22T14:00:00Z',
    null,
    null,
    '2026-03-20T08:00:00Z'
),
(
    'dala-inc',
    'Loyalty integration checklist',
    'Confirm retailer data imports before pilot launch.',
    'MEDIUM',
    'OPEN',
    'Customer Success Desk',
    'WHATSAPP',
    'OPPORTUNITY',
    (select id from crm_opportunities where tenant_id = 'dala-inc' and name = 'Retail Loyalty Automation'),
    '2026-03-23T16:00:00Z',
    null,
    null,
    '2026-03-21T09:20:00Z'
),
(
    'dala-inc',
    'Fleet dashboard post-go-live review',
    'Completed rollout review and closed follow-up items.',
    'LOW',
    'RESOLVED',
    'Service Operations',
    'EMAIL',
    'ACCOUNT',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'Mosi Logistics'),
    '2026-03-19T12:00:00Z',
    null,
    '2026-03-19T11:00:00Z',
    '2026-03-18T10:30:00Z'
);

insert into crm_activities (tenant_id, type, subject, related_entity_type, related_entity_id, details, created_at) values
(
    'dala-inc',
    'TASK',
    'Prepare executive sponsor briefing',
    'OPPORTUNITY',
    (select id from crm_opportunities where tenant_id = 'dala-inc' and name = 'ZimEdu Admissions Rollout'),
    'Brief the ZimEdu executive sponsor on rollout milestones and change-management risks.',
    '2026-03-20T10:10:00Z'
),
(
    'dala-inc',
    'NOTE',
    'Confirm loyalty pilot timeline',
    'OPPORTUNITY',
    (select id from crm_opportunities where tenant_id = 'dala-inc' and name = 'Retail Loyalty Automation'),
    'Marketing and operations teams aligned on a two-week pilot launch plan.',
    '2026-03-21T09:45:00Z'
),
(
    'dala-inc',
    'MEETING',
    'Clinical operations workflow review',
    'ACCOUNT',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'Savanna Health Network'),
    'Reviewed patient intake workflow and support coverage for the launch period.',
    '2026-03-21T13:30:00Z'
),
(
    'dala-inc',
    'TASK',
    'Close post-go-live review items',
    'TICKET',
    (select id from service_ticket where tenant_id = 'dala-inc' and title = 'Fleet dashboard post-go-live review'),
    'Archive resolved issues and prepare the quarterly service summary.',
    '2026-03-19T11:10:00Z'
);

insert into communication_conversationrecord (
    tenant_id, name, channel_type, direction, participant, subject, message_body,
    related_entity_type, related_entity_id, created_at
) values
(
    'dala-inc',
    'Admissions rollout kickoff',
    'EMAIL',
    'OUTBOUND',
    'nomsa.moyo@zimedu.example.com',
    'Kickoff agenda and onboarding notes',
    'Sharing the implementation checklist and next-step owners for the admissions rollout.',
    'LEAD',
    (select id from crm_leads where tenant_id = 'dala-inc' and email = 'nomsa.moyo@zimedu.example.com'),
    '2026-03-20T08:45:00Z'
),
(
    'dala-inc',
    'Loyalty pilot approval',
    'WHATSAPP',
    'INBOUND',
    'tatenda.ncube@harareretail.example.com',
    'Pilot scope confirmed',
    'Confirmed that store ops and customer experience leads are ready for the loyalty pilot.',
    'TICKET',
    (select id from service_ticket where tenant_id = 'dala-inc' and title = 'Loyalty integration checklist'),
    '2026-03-21T10:15:00Z'
),
(
    'dala-inc',
    'Quarterly service review',
    'EMAIL',
    'OUTBOUND',
    'ops@mosilogistics.example.com',
    'Review summary and next actions',
    'Sent the completed service review summary with recommended follow-up improvements.',
    'ACCOUNT',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'Mosi Logistics'),
    '2026-03-19T12:15:00Z'
);

insert into marketing_audience_segment (tenant_id, name, source_type, criteria, estimated_size, active, created_at) values
('dala-inc', 'Qualified Education Prospects', 'LEADS', 'status in (QUALIFIED, CONTACTED) and industry = EDUCATION', 42, true, '2026-03-10T09:00:00Z'),
('dala-inc', 'Active Service Accounts', 'ACCOUNTS', 'open tickets > 0 or activity in last 14 days', 18, true, '2026-03-12T10:00:00Z'),
('dala-inc', 'Commerce Follow-Up Accounts', 'ACCOUNTS', 'quotes in proposal or invoices due in next 14 days', 9, true, '2026-03-16T11:30:00Z');

insert into marketing_campaign (
    tenant_id, name, channel_type, status, audience_segment_id, subject, body,
    scheduled_at, delivered_count, last_executed_at, created_at
) values
(
    'dala-inc',
    'Education Launch Readiness',
    'EMAIL',
    'SENT',
    (select id from marketing_audience_segment where tenant_id = 'dala-inc' and name = 'Qualified Education Prospects'),
    'Prepare for the next rollout wave',
    'A focused message on rollout readiness, stakeholder alignment, and implementation support.',
    '2026-03-18T08:00:00Z',
    38,
    '2026-03-18T08:00:00Z',
    '2026-03-17T15:10:00Z'
),
(
    'dala-inc',
    'Service Adoption Check-In',
    'WHATSAPP',
    'ACTIVE',
    (select id from marketing_audience_segment where tenant_id = 'dala-inc' and name = 'Active Service Accounts'),
    'Share your service priorities',
    'A short check-in campaign for accounts with live service workstreams.',
    '2026-03-23T09:30:00Z',
    0,
    null,
    '2026-03-21T12:20:00Z'
),
(
    'dala-inc',
    'Quarter-End Commercial Follow-Up',
    'EMAIL',
    'DRAFT',
    (select id from marketing_audience_segment where tenant_id = 'dala-inc' and name = 'Commerce Follow-Up Accounts'),
    'Close the quarter with a cleaner pipeline',
    'Targeted commercial follow-up for open proposals and upcoming invoice milestones.',
    '2026-03-25T07:45:00Z',
    0,
    null,
    '2026-03-22T07:10:00Z'
);

insert into service_knowledge_base_article (tenant_id, title, category, body, published, created_at) values
('dala-inc', 'Admissions rollout handoff checklist', 'IMPLEMENTATION', 'Checklist for handing over an education rollout from project delivery to customer success.', true, '2026-03-09T08:00:00Z'),
('dala-inc', 'Retail loyalty pilot troubleshooting', 'SUPPORT', 'Known issues and resolution steps for pilot-stage loyalty program launches.', true, '2026-03-11T09:40:00Z'),
('dala-inc', 'Quarterly service review template', 'OPERATIONS', 'Reusable agenda and data points for quarterly service review meetings.', true, '2026-03-15T13:00:00Z');

insert into service_canned_response (tenant_id, title, channel_type, category, body, created_at) values
('dala-inc', 'Implementation kickoff follow-up', 'EMAIL', 'IMPLEMENTATION', 'Thanks for the kickoff today. Attached is the rollout tracker, owners list, and next-step summary.', '2026-03-10T08:15:00Z'),
('dala-inc', 'Support resolution confirmation', 'EMAIL', 'SUPPORT', 'We have completed the requested fix and closed the ticket. Please reply if any related issue remains open.', '2026-03-12T11:00:00Z'),
('dala-inc', 'WhatsApp adoption nudge', 'WHATSAPP', 'ADOPTION', 'Checking in on adoption progress. Let us know if you want a quick enablement session this week.', '2026-03-19T10:25:00Z');

insert into commerce_product (tenant_id, name, description, unit_price, status, created_at) values
('dala-inc', 'Admissions Workflow Suite', 'CRM workflow bundle for admissions and student outreach teams.', 42000.00, 'ACTIVE', '2026-03-04T08:00:00Z'),
('dala-inc', 'Retail Loyalty Automation Pack', 'Automation templates and segmentation flows for loyalty engagement.', 28500.00, 'ACTIVE', '2026-03-05T08:30:00Z'),
('dala-inc', 'Field Service Command Center', 'Operations dashboard and service workflow package for logistics teams.', 36000.00, 'ACTIVE', '2026-03-06T09:00:00Z'),
('dala-inc', 'Clinical Engagement Workspace', 'Care coordination and patient communications workspace for healthcare teams.', 51000.00, 'ACTIVE', '2026-03-07T10:15:00Z');

insert into commerce_quote (tenant_id, account_id, name, amount, status, valid_until, created_at) values
(
    'dala-inc',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'Harare Retail Holdings'),
    'Retail Loyalty Automation Q2 Expansion',
    162500.00,
    'PROPOSAL',
    '2026-03-31T23:59:59Z',
    '2026-03-17T14:20:00Z'
),
(
    'dala-inc',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'Copperline Manufacturing'),
    'Factory Service Desk Expansion',
    128000.00,
    'APPROVED',
    '2026-03-28T23:59:59Z',
    '2026-03-18T09:50:00Z'
);

insert into commerce_invoice (tenant_id, account_id, invoice_number, amount, status, due_at, created_at) values
(
    'dala-inc',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'ZimEdu Group'),
    'DALA-INV-260321-01',
    85000.00,
    'ISSUED',
    '2026-03-29T23:59:59Z',
    '2026-03-21T08:30:00Z'
),
(
    'dala-inc',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'Mosi Logistics'),
    'DALA-INV-260318-02',
    46000.00,
    'PAID',
    '2026-03-25T23:59:59Z',
    '2026-03-18T10:45:00Z'
);

insert into aiassistant_aiinteraction (
    tenant_id, name, operation_type, source_type, source_id, prompt_text, output_text, model_name, created_at
) values
(
    'dala-inc',
    'Admissions rollout summary',
    'SUMMARIZE',
    'LEAD',
    (select id from crm_leads where tenant_id = 'dala-inc' and email = 'nomsa.moyo@zimedu.example.com'),
    'Summarize current admissions rollout status and next steps.',
    'Summary: rollout is on track, executive sponsor briefing due, and onboarding dependencies are clear.',
    'local-mock',
    '2026-03-20T11:20:00Z'
),
(
    'dala-inc',
    'Retail follow-up draft',
    'DRAFT',
    'ACCOUNT',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'Harare Retail Holdings'),
    'Draft a commercial follow-up email for the loyalty pilot expansion.',
    'Draft: confirm pilot success metrics, proposed rollout scope, and contracting next steps.',
    'local-mock',
    '2026-03-21T09:55:00Z'
),
(
    'dala-inc',
    'Fleet account health',
    'ACCOUNT_HEALTH',
    'ACCOUNT',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'Mosi Logistics'),
    'Assess account health for Mosi Logistics.',
    'Account health 87/100. Strong adoption, healthy support posture, and positive service feedback.',
    'local-mock',
    '2026-03-21T14:10:00Z'
),
(
    'dala-inc',
    'Next best action for Copperline',
    'RECOMMENDATION',
    'OPPORTUNITY',
    (select id from crm_opportunities where tenant_id = 'dala-inc' and name = 'Factory Service Desk Expansion'),
    'Recommend the next action to help close the Copperline opportunity.',
    'Recommended next action: schedule procurement review and share a final support coverage summary.',
    'local-mock',
    '2026-03-22T08:10:00Z'
);

insert into reporting_reportsnapshot (
    tenant_id, name, report_type, delivery_channel, schedule_cadence, status, snapshot_payload, generated_at, created_at
) values
('dala-inc', 'Weekly pipeline review', 'PIPELINE_OVERVIEW', 'EMAIL', 'WEEKLY', 'GENERATED', 'qualified=2;proposal=2;negotiation=1;pipeline=820500.00', '2026-03-21T17:00:00Z', '2026-03-21T17:00:00Z'),
('dala-inc', 'Service operations digest', 'SERVICE_OVERVIEW', 'EMAIL', 'WEEKLY', 'GENERATED', 'openTickets=2;resolvedTickets=1;overdueTickets=0', '2026-03-21T17:15:00Z', '2026-03-21T17:15:00Z'),
('dala-inc', 'Commercial collections watch', 'COMMERCE_OVERVIEW', 'EMAIL', 'MONTHLY', 'GENERATED', 'issuedInvoices=1;paidInvoices=1;activeQuotes=2', '2026-03-22T06:45:00Z', '2026-03-22T06:45:00Z');

insert into integration_integrationconnection (
    tenant_id, name, channel_type, provider, marketplace_app_key, marketplace_version, status, created_at
) values
('dala-inc', 'WhatsApp Cloud', 'WHATSAPP', 'META', 'meta-whatsapp-cloud', '1.0.0', 'CONNECTED', '2026-03-19T07:45:00Z');

insert into platform_customentitydefinition (
    tenant_id, name, api_name, plural_label, field_schema_json, active, created_at
) values
('dala-inc', 'Implementation Site', 'implementation_site', 'Implementation Sites', '{"siteCode":{"type":"TEXT"},"region":{"type":"TEXT"},"goLiveDate":{"type":"DATE"}}', true, '2026-03-18T08:40:00Z');

insert into platform_customentityrecord (
    tenant_id, definition_id, record_data_json, created_at
) values
(
    'dala-inc',
    (select id from platform_customentitydefinition where tenant_id = 'dala-inc' and api_name = 'implementation_site'),
    '{"siteCode":"HAR-EDU-01","region":"Harare","goLiveDate":"2026-04-05"}',
    '2026-03-19T09:05:00Z'
);

insert into workflow_workflowdefinition (
    tenant_id, name, trigger_type, trigger_filter, target_entity_type, target_entity_api_name,
    conditions_json, action_type, action_subject, action_details, action_config_json, active, created_at
) values
(
    'dala-inc',
    'Create readiness activity for approved quotes',
    'OPPORTUNITY_CREATED',
    null,
    'CUSTOM_ENTITY',
    'implementation_site',
    '{"stage":"PROPOSAL"}',
    'CREATE_ACTIVITY',
    'Review rollout readiness',
    'Create an implementation-readiness activity when a commercial milestone is reached.',
    '{"priority":"HIGH"}',
    true,
    '2026-03-20T07:50:00Z'
);

insert into audit_logs (tenant_id, actor, action, entity_type, entity_id, summary, created_at) values
(
    'dala-inc',
    'Takudzwa Chitsungo',
    'CREATE',
    'ACCOUNT',
    (select id from crm_accounts where tenant_id = 'dala-inc' and name = 'ZimEdu Group'),
    'Seeded anchor account for education pipeline coverage.',
    '2026-03-22T08:40:00Z'
),
(
    'dala-inc',
    'Takudzwa Chitsungo',
    'CREATE',
    'OPPORTUNITY',
    (select id from crm_opportunities where tenant_id = 'dala-inc' and name = 'Retail Loyalty Automation'),
    'Seeded commercial pipeline opportunity for retail expansion testing.',
    '2026-03-22T08:41:00Z'
),
(
    'dala-inc',
    'Takudzwa Chitsungo',
    'CREATE',
    'TICKET',
    (select id from service_ticket where tenant_id = 'dala-inc' and title = 'Admissions portal handoff'),
    'Seeded live service ticket for operational dashboard testing.',
    '2026-03-22T08:42:00Z'
),
(
    'dala-inc',
    'Takudzwa Chitsungo',
    'CREATE',
    'QUOTE',
    (select id from commerce_quote where tenant_id = 'dala-inc' and name = 'Retail Loyalty Automation Q2 Expansion'),
    'Seeded quote book for commerce testing.',
    '2026-03-22T08:43:00Z'
),
(
    'dala-inc',
    'Takudzwa Chitsungo',
    'CREATE',
    'WORKFLOW',
    (select id from workflow_workflowdefinition where tenant_id = 'dala-inc' and name = 'Create readiness activity for approved quotes'),
    'Seeded workflow automation example for platform testing.',
    '2026-03-22T08:44:00Z'
),
(
    'dala-inc',
    'Takudzwa Chitsungo',
    'CREATE',
    'CUSTOM_ENTITY',
    (select id from platform_customentitydefinition where tenant_id = 'dala-inc' and api_name = 'implementation_site'),
    'Seeded custom entity definition for extension-platform testing.',
    '2026-03-22T08:45:00Z'
);

commit;
