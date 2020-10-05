insert into USER(name) values('ABC');

insert into USER(name) values('XYZ');

insert into HOTEL(name, location) values('Hyatt', 'Mumbai');

insert into HOTEL(name, location) values('Taj', 'Mumbai');

insert into ROOMTYPE(roomType, cost) values('DELUXE', 20);

insert into ROOMTYPE(roomType, cost) values('PLATINUM', 40);

insert into ROOMTYPE(roomType, cost) values('BUSINESS', 50);

insert into HOTELROOM(number, hotelId, roomTypeId) values(1, 1, 1);

insert into HOTELROOM(number, hotelId, roomTypeId) values(2, 1, 2);

insert into HOTELROOM(number, hotelId, roomTypeId) values(1, 2, 2);

insert into HOTELROOM(number, hotelId, roomTypeId) values(2, 2, 1);

insert into hotelroombooking(hotelId, hotelRoomId, bookingDate, bookingStatus, userId) 
values(1, 1, now(), 'BOOKED', 1);