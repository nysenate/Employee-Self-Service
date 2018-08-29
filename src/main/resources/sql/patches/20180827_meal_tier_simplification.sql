alter table ess.travel.meal_tier
  add column total text;

update ess.travel.meal_tier
set total = '46' where tier = '51';

update ess.travel.meal_tier
set total = '49' where tier = '54';

update ess.travel.meal_tier
set total = '54' where tier = '59';

update ess.travel.meal_tier
set total = '59' where tier = '64';

update ess.travel.meal_tier
set total = '64' where tier = '69';

update ess.travel.meal_tier
set total = '69' where tier = '74';

alter table ess.travel.meal_tier
  alter column total set not null;

alter table ess.travel.meal_tier
  drop column breakfast;

alter table ess.travel.meal_tier
  drop column lunch;

alter table ess.travel.meal_tier
  drop column dinner;
