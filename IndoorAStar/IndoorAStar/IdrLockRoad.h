#ifndef _IDRLOCKROAD_H_
#define _IDRLOCKROAD_H_

#include "prototype.h"

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <windows.h>

#define MAXLOCKHEADER						(12)	//head size;
#define MAXLOCKFLOOR						(4)		//hash size;
#define MAXLOCKEDGE							(20)	//hash size;

#define GET_LOCK_EDGE_SUCCESS				(0)		//get Lock Edge Info success
#define FLOOR_EDGE_NUM_ZERO					(1)		//floor edge number = 0

#define GET_FLOOR_EDGE_NUM_SUCCESS			(0)
#define FLOOR_EQUAL_GROUND					(1)

#define INITIAL_LOCK_ROAD_SUCCESS			(0)
#define INITIAL_FILE_READ_FAIL				(1)

#define GET_CLOSEST_EDGE_FAIL				(-1)

#define START_LOCK_ROAD_SUCCESS				(0)
#define FINISH_LOCK_ROAD_SUCCESS			(0)
#define FREE_LOCK_ROAD_SUCCESS				(0)

typedef struct III_RTPOINT     //Point information for routing
{
    III_POINT pntOriginal;          // original point
    III_POINT pntFix;           // The point after map matching
    III_INT nLineSegIdxFix;          // The edge id after map matching
    III_POINT pntFPoint;          // The Front node of the fix edge
    III_POINT pntTPoint;           // The Tail node of the fix edge
} III_RTPOINT;

typedef struct III_POINT     //Point information
{
    III_INT longitude;          // The longitude of the point
    III_INT latitude;           // The latitude of the point
    III_INT height;          // The height of the point
} III_POINT;

typedef struct _LockEdge {
	III_UINT lEdgeID;
	//Coordinate cdnt;
	III_POINT fPoint;//Coordinate
	III_POINT tPoint;//Coordinate
}LockEdge;

typedef struct _LockEdgeTable {
	III_UINT floorEdgeNum;
	LockEdge *lockEdge;
}LockEdgeTable;

typedef struct _IndoorLockRoad {
	III_INT maxE, minF, maxF, buildingID;
	III_CHAR *binName;//.bin file name
	FILE *lockfile;
	LockEdgeTable leTable;
}IndoorLockRoad;

#if defined __cplusplus
extern "C" {
#endif

int ComputeDist(int x, int y, int AX, int AY, int BX, int BY, int *mx, int *my, int *angle);
int GetAngle(int fx, int fy, int tx, int ty);


int startLockRoad(III_RTPOINT pnt, char *lockName);
//int startLockRoad(int lontitude, int latitude, int floor, char *lockName);
int initialLockRoad(IndoorLockRoad *idrLR, char *lockName);

int getFloorEdgeNum(IndoorLockRoad *idrLR, int floor);
int getLockEdgeInfo(IndoorLockRoad *idrLR, int floor);
int getClosestEdge(int x, int y, int AX, int AY, int BX, int BY, int *mx, int *my, int *angle);

int freeLockRoad(IndoorLockRoad *idrLR);

#ifdef __cplusplus
  }
#endif

#endif/* IDRLOCKROAD_H_ */