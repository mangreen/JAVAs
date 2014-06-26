/*
 * Route_IdrAStar_Out.h - Porting Layer for III Navi system
 *
 * (c)2010 Institute for Information Industry, All Rights Reserved
 *
 * Maintainer: mikeLin@iii.org.tw
 *
 * CVS: $Id: Route_IdrAStar_Out.h, v1.5 2010/05/05 04:16:00 MikeLin Exp $
 */
#ifndef _ROUTE_IDRASTAR_OUT_H_
#define _ROUTE_IDRASTAR_OUT_H_

#include "prototype.h"

/*
 * Error Codes
 */

#define CLLISTNOTADDED				(0)		//node has not added CloseList
#define CLLISTADDED					(1)		//node has added CloseList
#define HASHGETSUCCESS				(0)		//get Hash success
#define HASHGETFAIL					(1)		//get Hash fail
#define HASHNEWNODE					(0)		//add new Node to Hash
#define HASHREFRASHNODE				(1)		//refrash Node info in Hash
#define HASHGETGFAIL				(-1)	//get G value from Hash fail


/*
 * Route_IdrAStar out interface functions
 */

#if defined(__cplusplus)

extern "C" {
#endif



#if defined(__cplusplus)
}

#endif

#endif/*_ROUTE_IDRASTAR_OUT_H_*/
