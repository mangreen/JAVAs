#ifndef _ROUTE_IDRASTAR_H_
#define _ROUTE_IDRASTAR_H_

#include "prototype.h"
//#include "III_win32.c"

#include <stdio.h>
#include <stdlib.h>
//#include <conio.h>
#include <string.h>

#define SWAP(x,y) {int t; t = x; x = y; y = t;}
#define MAXHASH						(1024)	//hash size
#define MAXHEAPTREE					(1024)	//heap size
#define MAXHEADER					(16)	//header size
#define MAXNEIGHBOR					(20)	//every neighbor size
#define MAXEDGE						(8)	//every neighbor size

typedef struct _Node {
	III_UINT nodeID;
	III_INT x, y, z;
	III_INT f, g, h;
	
	III_INT offset;
	III_INT amount;	//the neighbor amount of node 
	
	III_INT dist;	//distance
	III_INT attr;	//attribute
	III_INT pflag;	//passage flag
	
	III_UINT edgeID;
	struct _Node *parent;
}Node;

typedef struct _Heaptree {
	Node heap[MAXHEAPTREE];
	III_INT count;
}Heaptree;

typedef struct _HashNode {
	III_INT hashID;
	Node *hnode;
	struct _HashNode *next;

}HashNode;

typedef struct _IndoorAStar {
	III_INT maxN, maxE, fullsize, dxdyScale, distScale, mapScale, buildingID;
	Heaptree opList;
	HashNode *nodeCache;
	III_CHAR *cllist;
	III_INT clSize;
	FILE *binfile;
	Node start, end;
	III_INT fID, tID;

}IndoorAStar;

#if defined __cplusplus
extern "C" {
#endif

III_INT initialAStar(IndoorAStar *);
III_INT finishAStar(IndoorAStar *);
III_INT setIdrAStarStart(IndoorAStar *, III_INT, III_INT, III_INT, III_INT);
III_INT	setIdrAStarEnd(IndoorAStar *, III_INT, III_INT, III_INT, III_INT);
III_INT setIdrRTName();
III_INT getIdrRTName();
III_INT getHeader(FILE *);

III_INT addCloseList(IndoorAStar *, III_INT, III_INT);
III_INT checkCloseList(IndoorAStar *, III_INT, III_INT);

III_INT addHash(HashNode[], Node *, III_INT);
III_INT getHash(HashNode[], Node *, III_INT);
III_INT getHashGValue(HashNode[], III_INT);
III_INT freeHash(HashNode hash[]);

III_INT insertHeaptree(Heaptree *, Node *);
III_INT deleteHeaptreeRoot(Heaptree *);
III_INT printHeaptree(Heaptree);
III_INT swapHeap(Node[], III_INT, III_INT);

III_INT startIdrAStar(IndoorAStar*, Node *);
III_INT getIdrNeighbor(IndoorAStar*, Node*, III_INT, III_INT);

#ifdef __cplusplus
  }
#endif

#endif/* INDOORASTAR_H_ */