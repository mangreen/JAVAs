/*
 * III_win32.h - III Navigation Porting Layer for Win32/CE platforms
 *
 * (c)2007 Institute for Information Industry, All Rights Reserved
 *
 * Maintainer: edwardc@iii.org.tw
 *
 * CVS: $Id: III_win32.h,v 1.2 2007/09/06 09:51:20 harold Exp $
 */
#ifndef _III_WIN32_H_
#define _III_WIN32_H_

#define III_PORTING_WIN32

/**
 * add corresponding OS/platform depend header files below
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
/**
 * add corresponding OS/platform definitions below
 */
  
//#define USECACHE
#if defined(III_64BIT)
  #undef  III_64BIT
  #define III_64BIT   _int64
#endif

#if defined(III_U64BIT)
  #undef  III_U64BIT
  #define III_U64BIT  unsigned _int64
#endif
    
#endif/*_III_WIN32_H_*/
