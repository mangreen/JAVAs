#include "Route_IdrAStar.h"
#include "Route_IdrAStar_Out.h"

#include "IdrLockRoad.h"

const III_CHAR BITMASK[8] = {1, 2, 4, 8, 16, 32, 64, 128};// 用來幫助設定bit array: {00000001, 00000010, 00000100, 00001000, 00010000, 00100000, 01000000, 10000000}

int main(void) {
	III_CHAR s[1];//for test console pause
	
	/*HashNode hash[MAXHASH] = {NULL};//hash初始值為零
	Node n;
	Node n1;
	Node n2;
	Node n3;
	Node start;
	Node end;*/
	
	//mangreen
	//IndoorAStar idras;
	
	/*testing code, A* core test
	start.nodeID = 0;
	start.x = 121554737;
	start.y = 25058702;
	start.z = 1;
	start.f = 0;
	start.g = 0;
	start.h = 0;
	start.amount = 2;
	start.offset = 16;
	start.edgeID = -1;
	start.parent = NULL;
	start.pflag;
	start.attr;
	start.dist;
	
	end.nodeID = 25;
	end.x = 121554735;
	end.y = 25058721;
	end.z = 13;
	end.f = 0;
	end.g = 0;
	end.h = 0;
	end.amount = 1;
	end.offset = 1116;
	end.pflag;
	end.attr;
	end.dist;
	end.edgeID;
	end.parent;*/
	
	//mangreen
	/*
	initialAStar(&idras);
	setIdrAStarStart(&idras, 0, 121554737, 25058702, 1);
	setIdrAStarEnd(&idras, 27, 121554735, 25058721, 13);
	startIdrAStar(&idras, &(idras.start));
	*/

	//idras.start = start;
	
	/*
	idras.end = end;
	idras.fID = end.nodeID;
	idras.tID = end.nodeID;
	
	*/
	/*testing code, hash test
	n1.f = 1;
	n1.g = 11;
	addHash(hash, &n1, 1);

	n2.f = 2;
	n2.g = 22;
	addHash(hash, &n2, 1025);

	if(getHash(hash, &n, 1) == 0){
		printf("\n%d", n.f);
		if(-1 != getHashGValue(hash, 1)){
			printf("  %d", getHashGValue(hash, 1));
		}
	}

	if(getHash(hash, &n, 1025) == 0){
		printf("\n%d", n.f);
		if(-1 != getHashGValue(hash, 1025)){
			printf("  %d", getHashGValue(hash, 1025));
		}
	}

	n3.f = 3;
	n3.g = 33;
	addHash(hash, &n3, 2049);

	if(getHash(hash, &n, 2049) == 0){
		printf("\n%d", n.f);
		if(-1 != getHashGValue(hash, 2049)){
			printf("  %d", getHashGValue(hash, 2049));
		}
	}

	freeHash(hash);*/

	/*testing code, close list test
	addCloseList(&idras, idras.clSize, 0);
	printf("\n%d", checkCloseList(&idras, idras.clSize, 0));
	*/

	//mangreen
	//finishAStar(&idras);

	/*
	int x = 121554811; 
	int y = 25058692;

	int AX = 121554824; 
	int AY = 25058718; 
	int BX = 121554827;
	int BY = 25058694; 
	int mx = 0; 
	int my = 0; 
	int angle = 0;
	int dist = 0;
	dist = ComputeDist(x, y, AX, AY, BX, BY, &mx, &my, &angle);
	printf("dist: %d, mx: %d, my: %d, angle: %d", dist, mx, my, angle);
	//printf("dist: %d", dist);
	//printf("mx: %d", *mx);*/

	startLockRoad(0, 0, 13, "lock.bin");

	scanf("%s", s);//for test console pause
	return 0;
}

//set Start of indoor A*
III_INT setIdrAStarStart(IndoorAStar* idras, III_INT fixEdgeID, III_INT fixX, III_INT fixY, III_INT fixZ){
	III_UCHAR egbuf[MAXNEIGHBOR]; //read file buffer(edge)
	Node fnode;//front node
	Node tnode;//tail node
	III_INT dx;
	III_INT dy;
	III_INT i;//for loop

	III_fseek(idras->binfile, idras->fullsize+(fixEdgeID*MAXEDGE), 0); //shift off1(fullsize) + MAZEDGE to read edge info

	if(!feof(idras->binfile)){	
		
		III_fread(&egbuf, sizeof(III_CHAR), MAXNEIGHBOR, idras->binfile);//file read

		//get dx
		if(egbuf[1] > 0x80){
			dx = ((0xffffff00 | egbuf[1]) << 8) | (0x000000ff & egbuf[0]);
		}else{
			dx = ((0x000000ff & egbuf[1]) << 8) | (0x000000ff & egbuf[0]);
		}
		//printf("\n dx:%d", dx);

		//get dy
		if(egbuf[3] > 0x80){
			dy = ((0xffffff00 | egbuf[3]) << 8) | (0x000000ff & egbuf[2]);
		}else{
			dy = ((0x000000ff & egbuf[3]) << 8) | (0x000000ff & egbuf[2]);
		}
		//printf("\n dy:%d", dy);

		//get offset
		fnode.offset = ((0x000000ff & egbuf[7]) << 20) | ((0x000000ff & egbuf[6]) << 12) | ((0x000000ff & egbuf[5]) << 4) | ((0x000000ff & egbuf[4]) >> 4);
		//printf("\n offset:%d", fnode.offset);

		//get amount
		fnode.amount = 0x0000000f & egbuf[4];
		//printf("\n amount:%d", fnode.amount);
	}

	for(i=0; i<fnode.amount; i++){
		getIdrNeighbor(idras, &tnode, fnode.offset, i);
		if(tnode.edgeID == fixEdgeID){
			tnode.g = 0;
			tnode.h = 0;
			tnode.f = tnode.g + tnode.h;
			tnode.edgeID = -1;
			tnode.parent = NULL;
			insertHeaptree(&(idras->opList), &tnode);//put into open list
			break;
		}
	}

	for(i=0; i<tnode.amount; i++){
		getIdrNeighbor(idras, &fnode, tnode.offset, i);
		if(fnode.edgeID == fixEdgeID){
			fnode.g = 0;
			fnode.h = 0;
			fnode.f = fnode.g + fnode.h;
			fnode.edgeID = -1;
			fnode.parent = NULL;
			insertHeaptree(&(idras->opList), &fnode);//put into open list
			break;
		}
	}

	idras->start = tnode;
	return 0;
}

//set End of indoor A*
III_INT setIdrAStarEnd(IndoorAStar* idras, III_INT fixEdgeID, III_INT fixX, III_INT fixY, III_INT fixZ){
	III_UCHAR egbuf[MAXNEIGHBOR]; //read file buffer(edge)
	Node fnode;//front node
	Node tnode;//tail node
	III_INT dx;
	III_INT dy;
	III_INT i;//for loop

	III_fseek(idras->binfile, idras->fullsize+(fixEdgeID*MAXEDGE), 0); //shift off1(fullsize) + MAZEDGE to read edge info

	if(!feof(idras->binfile)){	
		
		III_fread(&egbuf, sizeof(III_CHAR), MAXNEIGHBOR, idras->binfile);//file read

		//get dx
		if(egbuf[1] > 0x80){
			dx = ((0xffffff00 | egbuf[1]) << 8) | (0x000000ff & egbuf[0]);
		}else{
			dx = ((0x000000ff & egbuf[1]) << 8) | (0x000000ff & egbuf[0]);
		}
		//printf("\n dx:%d", dx);

		//get dy
		if(egbuf[3] > 0x80){
			dy = ((0xffffff00 | egbuf[3]) << 8) | (0x000000ff & egbuf[2]);
		}else{
			dy = ((0x000000ff & egbuf[3]) << 8) | (0x000000ff & egbuf[2]);
		}
		//printf("\n dy:%d", dy);

		//get offset
		fnode.offset = ((0x000000ff & egbuf[7]) << 20) | ((0x000000ff & egbuf[6]) << 12) | ((0x000000ff & egbuf[5]) << 4) | ((0x000000ff & egbuf[4]) >> 4);
		//printf("\n offset:%d", fnode.offset);

		//get amount
		fnode.amount = 0x0000000f & egbuf[4];
		//printf("\n amount:%d", fnode.amount);
	}

	for(i=0; i<fnode.amount; i++){
		getIdrNeighbor(idras, &tnode, fnode.offset, i);
		if(tnode.edgeID == fixEdgeID){
			tnode.g = 0;
			tnode.h = 0;
			tnode.f = tnode.g + tnode.h;
			idras->tID = tnode.nodeID;
			break;
		}
	}

	for(i=0; i<tnode.amount; i++){
		getIdrNeighbor(idras, &fnode, tnode.offset, i);
		if(fnode.edgeID == fixEdgeID){
			fnode.g = 0;
			fnode.h = 0;
			fnode.f = fnode.g + fnode.h;
			idras->fID = fnode.nodeID;
			break;
		}
	}

	idras->end.x = fixX;
	idras->end.y = fixY;
	idras->end.z = fixZ;

	return 0;
}

//start indoor A* routing
III_INT startIdrAStar(IndoorAStar* idras, Node* stay){
	
	III_INT i;
	Node neighbor;

	for(i = 0; i < stay->amount; i++){
		getIdrNeighbor(idras, &neighbor, stay->offset, i);
		
		if(0 == checkCloseList(idras, idras->clSize, neighbor.nodeID)){
			
			neighbor.g = stay->g + neighbor.dist;//set G value
			neighbor.h = fabs(idras->end.x - neighbor.x) + fabs(idras->end.y - neighbor.y) + 70*fabs(idras->end.z - neighbor.z);//使用曼哈頓方法（Manhattan method）計算H值
			neighbor.f = neighbor.g + neighbor.h; //F = G + H
			
			//處理更改parent, 搜尋neighbor的鄰居, 以G值查詢是否在hash中, 如果新G值比較小或是不在hash中就加入open list 
			if(-1 == getHashGValue(idras->nodeCache, neighbor.nodeID) || getHashGValue(idras->nodeCache, neighbor.nodeID) > neighbor.g){
				neighbor.parent = stay;//set parent
				insertHeaptree(&(idras->opList), &neighbor);//put into open list
				addHash(idras->nodeCache, &neighbor, neighbor.nodeID);//put into hash
			}
		}
	}

	addCloseList(idras, idras->clSize, stay->nodeID);//add Stay it to Close List

	//check End has been to or not, if not,  startIdrAStar carry on
	if(1 == checkCloseList(idras, idras->clSize, idras->fID) || 1 == checkCloseList(idras, idras->clSize, idras->tID)){
	
		/*testing code, print routing result*/
		printf("\npath: %d <-(%d)-", stay->nodeID, stay->edgeID);
		while(NULL != stay->parent){
			printf(" %d <-(%d)- ", stay->parent->nodeID, stay->parent->edgeID);
			stay = stay->parent;
		}

	}else{
		//get most small G value Node from open list
		Node next = idras->opList.heap[1];
		deleteHeaptreeRoot(&(idras->opList));

		startIdrAStar(idras, &next);//recursive call startIdrAStar
	}
	
	return 0;
}

//get Neighbor info of Node
III_INT getIdrNeighbor(IndoorAStar* idras, Node* neighbor, III_INT offset, III_INT order){
	
	III_UCHAR nbbuf[MAXNEIGHBOR]; //read file buffer(neighbor)
	Node newneighbor;

	III_fseek(idras->binfile, offset+(order*20), 0); //shift offset + order to read info

	if(!feof(idras->binfile)){	
		III_fread(&nbbuf, sizeof(III_CHAR), MAXNEIGHBOR, idras->binfile);//file read

		//get distance		
		newneighbor.dist = ((0x000000ff & nbbuf[1]) << 8) + (0x000000ff & nbbuf[0]);
		//printf("\n dist:%d", newneighbor.dist);

		//get floor
		newneighbor.z = nbbuf[2];
		//printf("\n floor:%d", newneighbor.z);

		//get attr
		newneighbor.attr = 0x000000ff & nbbuf[3];
		//printf("\n attribute:%d", newneighbor.attr);

		//get offset
		newneighbor.offset = ((0x000000ff & nbbuf[7]) << 20) + ((0x000000ff & nbbuf[6]) << 12) + ((0x000000ff & nbbuf[5]) << 4) + ((0x000000ff & nbbuf[4]) >> 4);
		//printf("\n offset:%d", newneighbor.offset);

		//get amount
		newneighbor.amount = 0x0000000f & nbbuf[4];
		//printf("\n amount:%d", newneighbor.amount);

		//get nodeID
		newneighbor.nodeID = ((0x000000ff & nbbuf[11]) << 6) + ((0x000000ff & nbbuf[10]) >> 2);
		//printf("\n nodeID:%d", newneighbor.nodeID);

		//get edgeID
		newneighbor.edgeID = ((0x00000003 & nbbuf[10]) << 12) + ((0x000000ff & nbbuf[9]) << 4) + ((0x000000ff & nbbuf[8]) >> 4);
		//printf("\n edgeID:%d", newneighbor.edgeID);

		//get passage flag
		newneighbor.pflag = 0x0000000f & nbbuf[8] ;
		//printf("\n pflag:%d", newneighbor.pflag);

		//get lontitude
		newneighbor.x = ((0x000000ff & nbbuf[15]) << 24) + ((0x000000ff & nbbuf[14]) << 16) + ((0x000000ff & nbbuf[13]) << 8) + (0x000000ff & nbbuf[12]);
		//printf("\n x:%d", newneighbor.x);

		//get latitude
		newneighbor.y = ((0x000000ff & nbbuf[19]) << 24) + ((0x000000ff & nbbuf[18]) << 16) + ((0x000000ff & nbbuf[17]) << 8) + (0x000000ff & nbbuf[16]);
		//printf("\n y:%d", newneighbor.y);
		
		memcpy(neighbor, &newneighbor, sizeof(Node));    // 將該資料拷至指定位置
	}

	return 0;
}

//put Node into Hash, 0 is new Node, 1 is refrash Node info
III_INT addHash(HashNode hash[], Node *node, III_INT nodeID){
	
	III_INT hashNum = nodeID % MAXHASH;
	HashNode *hashnode;
	HashNode newnode;
	
	hashnode = &(hash[hashNum]);

	if(NULL == hash[hashNum].hnode){//if hash[] is empty, put into array
		newnode.hashID = nodeID;
		newnode.hnode = node;
		newnode.next = NULL;
		hash[hashNum] = newnode;
		return 0;
	}else if(nodeID == hash[hashNum].hashID){//if Node is exist(hashID equal to nodeID), refrash Node info.
		hash[hashNum].hnode = node;
		return 1;
	}else{//if hash[] is not empty, add to backward by linking list
		hashnode = &(hash[hashNum]);

		while(NULL != hashnode->next){
			hashnode = hashnode->next;

			if(nodeID == hashnode->hashID){//if Node is exist(hashID equal to nodeID), refrash Node info.
				memcpy(hashnode->hnode, node, sizeof(Node));    // 將該資料拷至指定位置
				return 1;
			}
		}

		newnode.hashID = nodeID;
		newnode.hnode = node;
		newnode.next = NULL;
		hashnode->next = (HashNode*)III_malloc(sizeof(HashNode));   // 弄個新空間;
		memcpy(hashnode->next, &newnode, sizeof(HashNode));    // 將該資料拷至指定位置
		return 0;
	}	
}

//get Node from Hash, 0 is geted, 1 is not.
III_INT getHash(HashNode hash[], Node *node, III_INT nodeID){
	
	III_INT hashNum = nodeID % MAXHASH;
	HashNode *hashnode = &(hash[hashNum]);

	while(NULL != hashnode->hnode){
		if(hashnode->hashID == nodeID){
			memcpy(node, hashnode->hnode, sizeof(Node));    // 將該資料拷至指定位置
			return 0;
		}
		hashnode = hashnode->next;
	}

	node = NULL;
	return 1;
}

//get G value of Node from Hash, -1 is not in Hash.
III_INT getHashGValue(HashNode hash[], III_INT nodeID){
	III_INT hashNum = nodeID % MAXHASH;
	HashNode *hashnode = &(hash[hashNum]);

	while(NULL != hashnode->hnode){
		if(hashnode->hashID == nodeID){
			return hashnode->hnode->g;
		}
		hashnode = hashnode->next;
	}

	return -1;
}

//Add node to close list.
III_INT addCloseList(IndoorAStar *idras, III_INT clSize, III_INT nodeID){
	III_INT listShift = nodeID / 8;
	III_INT maskShift = nodeID % 8;
	//printf("\n ID:%d list:%d mask:%d", nodeID, listShift, maskShift);
	//printf("\n mask:%2X", *(idras->cllist + listShift));
	*((idras->cllist) + listShift) = *((idras->cllist) + listShift) | BITMASK[maskShift];
	//printf("\n mask:%2X", *(idras->cllist + listShift));
	//getch();
	return 0;
}

//Check that node have added to close list. 0 is added. 1 is not.
III_INT checkCloseList(IndoorAStar *idras, III_INT clSize, III_INT nodeID){
	III_INT listShift = nodeID / 8;
	III_INT maskShift = nodeID % 8;
	III_CHAR cmp = *((idras->cllist) + listShift) & BITMASK[maskShift];

	if(0 == strncmp(&(BITMASK[maskShift]), &cmp, 1)){
		return 1;
	}else{
		return 0;
	}
	
}

//Initial A*, read header of .bin file.
III_INT initialAStar(IndoorAStar *idras){
	III_UCHAR headbuf[MAXHEADER]; //read file buffer(header)
	HashNode hash[MAXHASH] = {NULL};//hash初始值為零
	III_INT i; //for loop

	idras->binfile = fopen("test.bin", "rb");
	if(!idras->binfile) { 
        puts("無法讀入檔案");
		//getch();
        return 1; 
    }

	if(!feof(idras->binfile)){ 
		III_fread(&headbuf, sizeof(III_CHAR), MAXHEADER, idras->binfile);

		//get maxN		
		idras->maxN = ((0x000000ff & headbuf[1]) << 8) + (0x000000ff & headbuf[0]);
		//printf("\n maxN:%d", idras->maxN);
		
		//set closelist amount by maxN
		idras->clSize = idras->maxN / 8; 
		if(idras->maxN % 8 > 0){
			idras->clSize++;
		}

		idras->cllist = (III_CHAR*)III_malloc(sizeof(III_CHAR)*(idras->clSize));//locate memery to close list	

		for(i=0; i<idras->clSize; i++){				
			*(idras->cllist + i) = 0;
		}

		//printf("\n cllist:%2X", idras->cllist);
		//printf("\n clSize:%d", idras->clSize);

		//get maxE		
		idras->maxE = ((0x000000ff & headbuf[3]) << 8) + (0x000000ff & headbuf[2]);
		//printf("\n maxE:%d", idras->maxE);

		//get fullsize
		idras->fullsize = ((0x000000ff & headbuf[7]) << 24) + ((0x000000ff & headbuf[6]) << 16) + ((0x000000ff & headbuf[5]) << 8) + (0x000000ff & headbuf[4]);
		//printf("\n fullsize:%d", idras->fullsize);

		//get dxdyScale
		idras->dxdyScale = 0x000000ff & headbuf[8];
		//printf("\n dxdyScale:%d", idras->dxdyScale);

		//get distScale
		idras->distScale = 0x000000ff & headbuf[9];
		//printf("\n distScale:%d", idras->distScale);

		//get mapScale 
		idras->mapScale = 0x000000ff & headbuf[10];
		//printf("\n mapScale:%d", idras->mapScale);

		//get buildingID
		idras->buildingID = ((0x000000ff & headbuf[14]) << 24) + ((0x000000ff & headbuf[13]) << 16) + ((0x000000ff & headbuf[12]) << 8) + (0x000000ff & headbuf[11]);
		//printf("\n buildingID:%d", idras->buildingID);
	}

	idras->opList.count = 0;
	idras->nodeCache = (HashNode*)III_malloc(sizeof(HashNode)*(MAXHASH));//locate memery to hash

	for(i=0; i<MAXHASH; i++){
		memset((idras->nodeCache + i), NULL, sizeof(HashNode));//initialize hash
	}

	return 0;
}

//release memory, clear start & goal, close .bin file
III_INT finishAStar(IndoorAStar *idras){
	III_INT i;//for loop
	
	III_fclose(idras->binfile);//close .bin file
	III_free(idras->cllist);//release close list
	freeHash(idras->nodeCache);
	III_free(idras->nodeCache);//release hash array
		
	return 0;
}

//release memory of hash
III_INT freeHash(HashNode hash[]){
	III_INT i;//for loop
	HashNode *hashnode;
	HashNode *nextnode;
	
	for(i=0; i<MAXHASH; i++){
		if(NULL != hash[i].next){
			nextnode = hash[i].next;
			while(NULL != nextnode->next){
				hashnode = nextnode;
				nextnode = nextnode->next;
				III_free(hashnode);
			}
			III_free(nextnode);
		}
	}
	return 0;
}

III_INT setIdrRTName(){
	return 0;
}

III_INT getIdrRTName(){
	return 0;
}

//testing code, get header
III_INT getHeader(FILE *file){ 
    
    III_UCHAR ch[16]; 
	III_UINT maxN, maxE, fullsize, dxdyScale, distScale, mapScale, buildingID;

	if(!feof(file)){ 
		III_fread(&ch, sizeof(char), 16, file);

		//get maxN		
		maxN = ( ((0x00000000 << 8) | (int)ch[1]) << 8) | (int)ch[0];
		printf("\n maxN:%d", maxN);


		//get maxE		
		maxE = ( ((0x00000000 << 8) | (int)ch[3]) << 8) | (int)ch[2];
		printf("\n maxN:%d", maxE);

		//get fullsize
		fullsize = ((0xffffff00 ^ ch[7]) << 24) | (ch[6] << 16) | (ch[5] << 8) | (int)ch[4];
		printf("\n fullsize:%d", fullsize);

		//get dxdyScale
		dxdyScale = (0x00000000 << 8) | (int)ch[8];
		printf("\n dxdyScale:%d", dxdyScale);

		//get distScale
		distScale = (0x00000000 << 8) | (int)ch[9];
		printf("\n distScale:%d", distScale);

		//get mapScale 
		mapScale = (0x00000000 << 8) | (int) ch[10];
		printf("\n mapScale:%d", mapScale);

		//get buildingID
		buildingID = (((0x00000000 << 8) | (int)ch[14]) << 24) | (ch[13] << 16) | (ch[12] << 8) | (int)ch[11];
		printf("\n buildingID:%d", buildingID);

		//getch();
	}

	III_fclose(file);
	return 0;
}

//insert node to heap
III_INT insertHeaptree(Heaptree *tree, Node *node){
	int p = tree->count/2; //tree[parent]
	int c = tree->count; //tree[child]

	tree->count = tree->count + 1;
	tree->heap[tree->count] = *node;

	while(p >= 1 && tree->heap[p].f > tree->heap[c].f) {
	   	swapHeap(tree->heap, p, c);
	    c = p;
	    p = c/2;
	}
	return 0;
}

//delete root of heap
III_INT deleteHeaptreeRoot(Heaptree *tree){
	int p = 1;
	int c = 2 * p;

	tree->heap[1] = tree->heap[tree->count];//root與 tail交換
	tree->count = tree->count - 1;//count-1

	//調整heap tree

    while(c <= tree->count) {
    	if(c < tree->count && tree->heap[c+1].f < tree->heap[c].f) {
    		c++;
	    }
	    if(tree->heap[p].f <= tree->heap[c].f) {
	    	break;
	    }
	    swapHeap(tree->heap, p, c);
	    p = c;
	    c = 2 * p;
	}
	//root = tree[1];
	return 0;
}

//testing code, print heap
III_INT printHeaptree(Heaptree tree){
	int i;

	printf("%d\n", tree.count);
	for(i = 1; i < tree.count + 1; i++){
		printf("%d, ",tree.heap[i].f);
	}
	printf("\n");
	return 0;
}

//swap node of heap
III_INT swapHeap(Node tree[], int i, int j){
	Node t = tree[i];
	tree[i] = tree[j];
	tree[j] = t;
	return 0;
}

