drop database if exists CovidDB;
create database CovidDB;
use CovidDB;

create table covid(
	region char(5) not null,
    confirmed int not null,
    death int not null,
    conPercentage decimal(5,2) null,
    deathPercentage decimal(5,2) null,
    rating varchar(4) null,
	ranking tinyint(2) null,
    inoculation decimal(5,2) null,
    primary key(region)
);

insert into covid values('서울특별시', 4803181, 5464, 19.49, 19.34, '위험', 2, 17.62);
insert into covid values('경기도', 6616190, 7100, 26.85, 25.13, '매우위험', 1);
insert into covid(region, confirmed, death, conpercentage, deathPercentage, ranking) values ('부산광역시', 1466729, 2455, 15.23, 14.89, 3);

create index index_region on covid (region);
show index from covid;

select * from covid;

select * from covid order by confirmed desc;

select * from covid order by death asc;

select * from covid where conPercentage = (select max(conPercentage) from covid);
select * from covid where conPercentage = (select min(conPercentage) from covid);

drop procedure if exists prosedure_insert_covid;
delimiter $$
create procedure procedure_insert_covid(
	In in_region char(5),
    In in_confirmed int,
    In in_death int
)
begin
	declare in_conPercentage double;
    declare in_deathPercentage double;
	declare in_rating varchar(4); 
    declare in_ranking tinyint;
    -- 전국 확진자 수 => 해당 지역비율
    SET in_conPercentage = in_confirmed / 24634296.0 * 100.0;
    -- 전국 사망자 수 => 해당 지역비율
    SET in_deathPercentage = in_death / 28246.0 * 100.0;
    -- 감염 위험도
    SET in_rating =
		case
			when in_conPercentage >= 20.000 then '매우위험'
            when in_conPercentage >= 15.000 then '위험'
            when in_conPercentage >= 10.000 then '주의'
            when in_conPercentage >= 5.000 then '양호'
            else '안전지역'
            end;
           
		-- 삽입
        insert into covid(region, confirmed, death)
        values(in_region, in_confirmed, in_death);
        
        update covid set conPercentage = in_conPercentage, deathPercentage = in_deathPercentage, 
        rating = in_rating, ranking = in_ranking
        where region = in_region;
   
end $$
delimiter ;

delimiter !!
create procedure procedure_update_covid(
	In in_region char(5),
    In in_confirmed int,
    In in_death int
)
begin
	declare in_conPercentage double;
    declare in_deathPercentage double;
	declare in_rating varchar(4);
    declare in_ranking tinyint;
    SET in_conPercentage = in_confirmed / 24634296.0 * 100.0;
    SET in_deathPercentage = in_death / 28246.0 * 100.0;
    -- 감염 위험도
    SET in_rating =
		case
			when in_conPercentage >= 20.000 then '매우위험'
            when in_conPercentage >= 15.000 then '위험'
            when in_conPercentage >= 10.000 then '주의'
            when in_conPercentage >= 5.000 then '양호'
            else '안전지역'
            end;
update covid
set
	confirmed = in_confirmed,
	death = in_death,
	conPercentage = in_conPercentage,
	deathPercentage = in_deathPercentage,
	rating = in_rating,
	ranking = in_ranking
    WHERE
    region = in_region;
end !!
delimiter ;

use covidDB;
drop table deletecovid;
create table deletecovid(
	region char(5) not null,
    confirmed int not null,
    death int not null,
    conPercentage decimal(5,2) null,
    deathPercentage decimal(5,2) null,
    rating varchar(4),
	ranking tinyint null,
    deletedate datetime
);

create table updatecovid(
	region char(5) not null,
    confirmed int not null,
    death int not null,
    conPercentage decimal(5,2) null,
    deathPercentage decimal(5,2) null,
    rating varchar(4),
	ranking tinyint null,
    updatedate datetime
);

create table insertcovid(
	region char(5) not null,
    confirmed int not null,
    death int not null,
    conPercentage decimal(5,2) null,
    deathPercentage decimal(5,2) null,
    rating varchar(4),
	ranking tinyint null,
    updatedate datetime
);

delimiter !!
create trigger trg_insertcovid
	before insert
    on covid
    for each row
begin
	insert into insertcovid values (
		new.regin, 
        new.confiremd, 
        new.death, 
        new.conPercentage, 
        new.deathPercentage, 
        new.rating, 
        new.ranking, 
        now());
end !!
delimiter ;

delimiter !!
create trigger trg_deletecovid
	after delete
    on covid
    for each row
begin
	insert into deletecovid values
(old.region, old.confirmed, old.death, old.conPercentage, 
old.deathPercentage, old.rating, old.ranking, now());
end !!
delimiter ;

create trigger trg_updatecovid
	after delete
    on covid
    for each row
begin
	insert into updatecovid values
(old.region, old.confirmed, old.death, old.conPercentage, 
old.deathPercentage, old.rating, old.ranking, now())
end !!
delimiter ;

drop function if exists inoculateFunc;

SET GLOBAL log_bin_trust_function_creators = 1;

delimiter $$
create function inoculateFunc(
	value1 int
)
	returns decimal(5,2)
begin
	return value1 / 7468808 * 100;
end $$
delimiter ;

select	inoculateFunc (1250437) as '접종률(%)';
    
drop table deletecovid;