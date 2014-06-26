#include "IdrLockRoad.h"

int startLockRoad(III_RTPOINT pnt, char *lockName){
		IndoorLockRoad *idrLR = III_NULL;
	idrLR = (IndoorLockRoad*)III_malloc(sizeof(IndoorLockRoad));   // 弄個新空間;
	
	initialLockRoad(idrLR, lockName);
	getFloorEdgeNum(idrLR, floor);
	getLockEdgeInfo(idrLR, floor);

	//int edgeid = getClosestEdge(lontitude, latitude, idrLR, int *mx, int *my, int *angle, int dist);
	finishLockRoad(idrLR);

	return START_LOCK_ROAD_SUCCESS;
}
/*
int startLockRoad(int lontitude, int latitude, int floor, char *lockName){
	IndoorLockRoad *idrLR = III_NULL;
	idrLR = (IndoorLockRoad*)III_malloc(sizeof(IndoorLockRoad));   // 弄個新空間;
	
	initialLockRoad(idrLR, lockName);
	getFloorEdgeNum(idrLR, floor);
	getLockEdgeInfo(idrLR, floor);

	int edgeid = getClosestEdge(lontitude, latitude, idrLR, int *mx, int *my, int *angle, int dist);
	finishLockRoad(idrLR);

	return START_LOCK_ROAD_SUCCESS;
}*/

int finishLockRoad(IndoorLockRoad *idrLR){
	fclose(idrLR->lockfile);
	free(idrLR);
	return FINISH_LOCK_ROAD_SUCCESS;
}

int initialLockRoad(IndoorLockRoad *idrLR, char *lockName){
	III_UCHAR lockheadbuf[MAXLOCKHEADER]; //read file buffer(header)
	III_INT *intTemp = III_NULL;

	idrLR->lockfile = III_fopen(lockName, "rb");
	if(!idrLR->lockfile) { 
        puts("無法讀入檔案");
		//getch();	
        return INITIAL_FILE_READ_FAIL; 
    }

	// we could use fseek the file end to check the file size if less than header size
	fseek(idrLR->lockfile, 0, SEEK_END);

	if(ftell(idrLR->lockfile) > (MAXLOCKHEADER+MAXLOCKFLOOR+MAXLOCKEDGE)){

		fseek(idrLR->lockfile, SEEK_SET, 0); //move to file begin
		fread(lockheadbuf, sizeof(III_CHAR), MAXLOCKHEADER, idrLR->lockfile);

		//get maxE		
		idrLR->maxE = ((0x000000ff & lockheadbuf[1]) << 8) | (0x000000ff & lockheadbuf[0]);
		printf("\n maxLockE:%d", idrLR->maxE);

		//get minF
		if(0x80 == (0x80 & lockheadbuf[2])){
			idrLR->minF = (0xffffff00 | lockheadbuf[2]);

		}else{	
			idrLR->minF = (0x000000ff & lockheadbuf[2]);
		}
		printf("\n minLockF:%d", idrLR->minF);

		//get minF		
		idrLR->maxF = (0x000000ff & lockheadbuf[3]);
		printf("\n minLockF:%d", idrLR->maxF);

		//get buildingID
		intTemp = &lockheadbuf[4];
		idrLR->buildingID = *intTemp;
		//idras->header.buildingID = ((0x000000ff & headbuf[11]) << 24) | ((0x000000ff & headbuf[10]) << 16) | ((0x000000ff & headbuf[9]) << 8) | (0x000000ff & headbuf[8]);
		printf("\n lockBuildingID:%d", idrLR->buildingID);
	}

	return INITIAL_LOCK_ROAD_SUCCESS;
}

int getFloorEdgeNum(IndoorLockRoad *idrLR, int floor){
	III_UCHAR lfbuf[MAXLOCKFLOOR]; //read file buffer(lock floor number)
	III_INT *intTemp;

	//shift offset to read lock floor number
	if(floor > 0){
		III_fseek(idrLR->lockfile, MAXLOCKHEADER+(abs(idrLR->minF)+(floor-1))*MAXLOCKFLOOR, 0); //if floor > 0, skip header+minF+(floor-1) to move 
	}else if(floor < 0){
		III_fseek(idrLR->lockfile, MAXLOCKHEADER+(abs(floor)-1)*MAXLOCKFLOOR, 0); //if floor < 0, skip header+minF+(floor-1) to move 
	}else{
		return FLOOR_EQUAL_GROUND;
	}

	
	if(!feof(idrLR->lockfile)){	
		III_fread(lfbuf, sizeof(III_CHAR), MAXLOCKFLOOR, idrLR->lockfile);//file read

		//get edge number of floor
		intTemp = &lfbuf[0];
		idrLR->leTable.floorEdgeNum = *intTemp;
		//idrLR->leTable.floorEdgeNum = ((0x000000ff & nbbuf[3]) << 24) | ((0x000000ff & nbbuf[2]) << 16) | ((0x000000ff & nbbuf[1]) << 8) | (0x000000ff & nbbuf[0]);
		printf("\n floorEdgeNum:%d", idrLR->leTable.floorEdgeNum);
	}
	
	//memory locate lockEdge[idrLR->leTable.floorEdgeNum]
	idrLR->leTable.lockEdge = (LockEdge*)III_malloc(sizeof(LockEdge)*(idrLR->leTable.floorEdgeNum));   // 弄個新空間;

	return GET_FLOOR_EDGE_NUM_SUCCESS;
}

int getLockEdgeInfo(IndoorLockRoad *idrLR, int floor){
	III_UCHAR lebuf[MAXLOCKEDGE]; //read file buffer(lock edge info)
	III_INT *intTemp;
	III_INT i;
	LockEdge newLockEdge;
	III_INT count = idrLR->leTable.floorEdgeNum;

	if(idrLR->leTable.floorEdgeNum == 0){//if floorEdgeNum don't need to do anything
		return FLOOR_EDGE_NUM_ZERO;
	}

	for(i=0; i<idrLR->maxE; i++){

		III_fseek(idrLR->lockfile, MAXLOCKHEADER+((idrLR->maxF)-(idrLR->minF))*MAXLOCKFLOOR+i*MAXLOCKEDGE, 0); //if floor > 0, skip header+minF+(floor-1) to move

		if(!feof(idrLR->lockfile)){	
			III_fread(lebuf, sizeof(III_CHAR), MAXLOCKEDGE, idrLR->lockfile);//file read

			newLockEdge.lEdgeID = i;

			//get edge number of floor
			intTemp = &lebuf[0];
			newLockEdge.fPoint.longitude = *intTemp;
			//idrLR->leTable.floorEdgeNum = ((0x000000ff & nbbuf[3]) << 24) | ((0x000000ff & nbbuf[2]) << 16) | ((0x000000ff & nbbuf[1]) << 8) | (0x000000ff & nbbuf[0]);
			//printf("\n floorEdgeNum:%d", newLockEdge.fPoint.longitude);

			//get edge number of floor
			intTemp = &lebuf[4];
			newLockEdge.fPoint.latitude = *intTemp;
			//idrLR->leTable.floorEdgeNum = ((0x000000ff & nbbuf[3]) << 24) | ((0x000000ff & nbbuf[2]) << 16) | ((0x000000ff & nbbuf[1]) << 8) | (0x000000ff & nbbuf[0]);
			//printf("\n floorEdgeNum:%d", newLockEdge.fPoint.latitude);

			//get fPoint floor
			if(0x80 == (0x80 & lebuf[8])){
				newLockEdge.fPoint.height = (0xffffff00 | lebuf[8]);

			}else{	
				newLockEdge.fPoint.height = (0x000000ff & lebuf[8]);
			}
			//printf("\n floorEdgeNum:%d", newLockEdge.fPoint.height);

			//get tPoint floor
			if(0x80 == (0x80 & lebuf[11])){
				newLockEdge.tPoint.height = (0xffffff00 | lebuf[11]);

			}else{	
				newLockEdge.tPoint.height = (0x000000ff & lebuf[11]);
			}
			//printf("\n floorEdgeNum:%d", newLockEdge.tPoint.height);

			//get edge number of floor
			intTemp = &lebuf[12];
			newLockEdge.tPoint.longitude = *intTemp;
			//idrLR->leTable.floorEdgeNum = ((0x000000ff & nbbuf[3]) << 24) | ((0x000000ff & nbbuf[2]) << 16) | ((0x000000ff & nbbuf[1]) << 8) | (0x000000ff & nbbuf[0]);
			//printf("\n floorEdgeNum:%d", newLockEdge.tPoint.longitude);

			//get edge number of floor
			intTemp = &lebuf[16];
			newLockEdge.tPoint.latitude = *intTemp;
			//idrLR->leTable.floorEdgeNum = ((0x000000ff & nbbuf[3]) << 24) | ((0x000000ff & nbbuf[2]) << 16) | ((0x000000ff & nbbuf[1]) << 8) | (0x000000ff & nbbuf[0]);
			//printf("\n floorEdgeNum:%d", newLockEdge.tPoint.latitude);
		}

		if(newLockEdge.fPoint.height == newLockEdge.tPoint.height && newLockEdge.fPoint.height == floor){
			
			memcpy(((idrLR->leTable.lockEdge)+(idrLR->leTable.floorEdgeNum)-count), &newLockEdge, sizeof(LockEdge));
			count--;
		}
	}
	return GET_LOCK_EDGE_SUCCESS;
}

int getClosestEdge(int x, int y, IndoorLockRoad *idrLR, int *mx, int *my, int *angle, int dist){
	double dis, dot, D, minD = (double)(0x7fffffff), a1, a2, b1, b2;
	int i, tx, ty;
	int eID = -1;

	for (i=0; i<idrLR->leTable.floorEdgeNum; i++)     // for each point on the street except the last one (i.e., for each edge of the street)   
	{	
		a1 = ((idrLR->leTable.lockEdge)+i)->tPoint.longitude - ((idrLR->leTable.lockEdge)+i)->fPoint.longitude;
		a2 = ((idrLR->leTable.lockEdge)+i)->tPoint.latitude - ((idrLR->leTable.lockEdge)+i)->fPoint.latitude;
		b1 = x - ((idrLR->leTable.lockEdge)+i)->fPoint.longitude;
		b2 = y - ((idrLR->leTable.lockEdge)+i)->fPoint.latitude;
		dot = a1*b1 + a2*b2;    // inner product
		dis = dot/(a1*a1+a2*a2);    
		
		if(dot<=0.0f) { //D: dist of p to p1 (dot <= 0 means v(p1,p) and v(p1,p2) is perpendicular in opposite direction)
			D = (III_FLOAT)(x-((idrLR->leTable.lockEdge)+i)->fPoint.longitude)*(x-((idrLR->leTable.lockEdge)+i)->fPoint.longitude)+(III_FLOAT)(y-((idrLR->leTable.lockEdge)+i)->fPoint.latitude)*(y-((idrLR->leTable.lockEdge)+i)->fPoint.latitude);
			tx = ((idrLR->leTable.lockEdge)+i)->fPoint.longitude;
			ty = ((idrLR->leTable.lockEdge)+i)->fPoint.latitude;
		}else if(dis*dot>=a1*a1+a2*a2) { //D: dist of p to p2 (dis*dot = (dot^2)/(a1*a1+a2*a2) = v(p1,p)在v(p1,p2)上的投影分量(平方))
			D = (III_FLOAT)(x-((idrLR->leTable.lockEdge)+i)->tPoint.longitude)*(x-((idrLR->leTable.lockEdge)+i)->tPoint.longitude)+(III_FLOAT)(y-((idrLR->leTable.lockEdge)+i)->tPoint.latitude)*(y-((idrLR->leTable.lockEdge)+i)->tPoint.latitude);
			tx = ((idrLR->leTable.lockEdge)+i)->tPoint.longitude;
			ty = ((idrLR->leTable.lockEdge)+i)->tPoint.latitude;
		}else { //D: dist between p and the projection of p on edge(p1,p2)
			tx = ((idrLR->leTable.lockEdge)+i)->fPoint.longitude + (int)(dis*a1);
			ty = ((idrLR->leTable.lockEdge)+i)->fPoint.latitude + (int)(dis*a2);
			D = (III_FLOAT)(tx-x)*(tx-x)+(III_FLOAT)(ty-y)*(ty-y);
		}

		if(D<minD) {    // 存距離最小的資訊
			minD = D;
			*mx = tx;
			*my = ty;
			*angle = GetAngle(((idrLR->leTable.lockEdge)+i)->fPoint.longitude, ((idrLR->leTable.lockEdge)+i)->fPoint.latitude, ((idrLR->leTable.lockEdge)+i)->tPoint.longitude, ((idrLR->leTable.lockEdge)+i)->tPoint.latitude);
			eID = ((idrLR->leTable.lockEdge)+i)->lEdgeID;
		}
	} //end of for loop

	dist = ((int)sqrt(minD));
	//return ((int)sqrt(minD))*MAPSCALE;  // 回傳最短距離(unit: 0.1m)
	return eID;  // 回傳最短距離(unit: 0.1m)
}

int freeLockRoad(IndoorLockRoad *idrLR){

	III_free(idrLR->leTable.lockEdge);
	III_free(idrLR);

	return FREE_LOCK_ROAD_SUCCESS;
}




int ComputeDist(int x, int y, int AX, int AY, int BX, int BY, int *mx, int *my, int *angle)
{

	double dis, dot, D, minD = (double)(0x7fffffff), a1, a2, b1, b2;
	int i, tx, ty;
	
	a1 = BX - AX;
	a2 = BY - AY;
	b1 = x - AX;
	b2 = y - AY;
	dot = a1*b1 + a2*b2;    // inner product
	dis = dot/(a1*a1+a2*a2);    
	
	if(dot<=0.0f) { //D: dist of p to p1 (dot <= 0 means v(p1,p) and v(p1,p2) is perpendicular in opposite direction)
		D = (III_FLOAT)(x-AX)*(x-AX)+(III_FLOAT)(y-AY)*(y-AY);
		tx = AX;
		ty = AY;
	}else if(dis*dot>=a1*a1+a2*a2) { //D: dist of p to p2 (dis*dot = (dot^2)/(a1*a1+a2*a2) = v(p1,p)在v(p1,p2)上的投影分量(平方))
		D = (III_FLOAT)(x-BX)*(x-BX)+(III_FLOAT)(y-BY)*(y-BY);
		tx = BX;
		ty = BY;
	}else { //D: dist between p and the projection of p on edge(p1,p2)
		tx = AX + (int)(dis*a1);
		ty = AY + (int)(dis*a2);
		D = (III_FLOAT)(tx-x)*(tx-x)+(III_FLOAT)(ty-y)*(ty-y);
	}

	//if(D<minD) {    // 存距離最小的資訊
		minD = D;
		*mx = tx;
		*my = ty;
        *angle = GetAngle(AX, AY, BX, BY);
	//}

	//return ((int)sqrt(minD))*MAPSCALE;  // 回傳最短距離(unit: 0.1m)
	return ((int)sqrt(minD));  // 回傳最短距離(unit: 0.1m)
}

int GetAngle(int fx, int fy, int tx, int ty)
{
    int valid; 
    float slope;
	int dx = fx - tx, dy = fy - ty, ang;
	double angle;
    
    if((fx - tx) != 0)
    {
        valid = 1;
        slope = (float)(fy - ty)/(fx - tx);
    }
    else
        valid = 0;
    
    //cout << fx << "|" << fy << "|" << tx << "|" << ty << "|" << " ";

	angle = acos(abs(dx)/sqrt((double)dx*dx+dy*dy));
	angle *= 57.2957914;

    if(valid == 1 && slope < 0)
        ang = (int)(180 - angle);
    else
        ang = (int)angle;

	return ang;
}