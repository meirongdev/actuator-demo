-- devtools restarts reuse the same in-memory database, so start from a clean slate
delete from widget;

insert into widget (id, name) values (1, 'sprocket'), (2, 'flange'), (3, 'grommet');
