/*
 * prototype.h - Porting Layer for III Navi system
 *
 * (c)2010 Institute for Information Industry, All Rights Reserved
 *
 * Maintainer: mikeLin@iii.org.tw
 *
 * CVS: $Id: prototype.h,v 1.5 2010/01/04 04:16:00 MikeLin Exp $
 */
#ifndef _PROTOTYPE_H_
#define _PROTOTYPE_H_

#include <windows.h>
/*
 * Data Types
 */

//#define III_CHAR          signed char
#define III_CHAR          char
#define III_UCHAR         unsigned char

#define III_SHORT         signed short
#define III_USHORT        unsigned short

#define III_INT           /*signed*/ int
#define III_UINT          unsigned int

#define III_FLOAT         float
#define III_DOUBLE        double

#define III_LONG          long
#define III_ULONG         unsigned long

#define III_64BIT         void
#define III_U64BIT        void    /* use particular implementation regarding platforms */

#define III_BOOL          char    /* use char for boolean type in case lack of native `bool' support */
#define III_TRUE          1
#define III_FALSE         0

#define III_VOID          void

#define III_NULL          0       /* for 0x00000000 cases */
#define III_INVALID       -1	  /* for 0xffffffff cases */

#define III_S01           III_CHAR
#define III_U01           III_UCHAR
#define III_S02           III_SHORT
#define III_U02           III_USHORT
#define III_S04           III_INT
#define III_U04           III_UINT
#define III_S08           III_64BIT
#define III_U08           III_U64BIT


#define STORAGELOC  "Storage Card"   //"SDMMC"   // "Storage Card //APPS"  // "Àx¦s¥d"  //"SD¥d"  // ""
#define MAPDATAPATH  "..\\ShareFolder\\MapData\\"// ##STORAGELOC ""
#define THEMEPATH  "..\\ShareFolder\\MapData\\" //"\\" ##STORAGELOC "\\theme\\"
#define EDWARDUIPATH "..\\ShareFolder\\EdwardUI\\"

#define CUSLDMK_FILE "..\\ShareFolder\\MapData\\"  // "//" ##STORAGELOC "//MapData//CusLdMk.file"
#define SYSINFO_FILE "..\\ShareFolder\\MapData\\" // "//" ##STORAGELOC "//Setting//SysInfo.txt"
#define PREDEST_FILE "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\Setting\\predest.bin"
#define CONFIG_FILE "..\\ShareFolder\\MapData\\" // "//" ##STORAGELOC "//Setting//setting.conf"
#define DEFAULT_FILE "..\\ShareFolder\\MapData\\" // "//" ##STORAGELOC "//Setting//default.conf"
#define CUSLDMK_KML "..\\ShareFolder\\MapData\\" //  "\\" ##STORAGELOC "\\MapData\\CusLdMk.kml"

#define VOICENAVf1   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\f_v1.wav"
#define VOICENAVf2   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\f_v2.wav"
#define VOICENAVf3   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\f_v3.wav"
#define VOICENAVf4   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\f_v4.wav"
#define VOICENAVf5   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\f_v5.wav"
#define VOICENAVf6   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\f_v6.wav"
#define VOICENAVf7   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\f_v7.wav"
#define VOICENAVm1   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\m_v1.wav"
#define VOICENAVm2   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\m_v2.wav"
#define VOICENAVm3   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\m_v3.wav"
#define VOICENAVm4   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\m_v4.wav"
#define VOICENAVm5   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\m_v5.wav"
#define VOICENAVm6   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\m_v6.wav"
#define VOICENAVm7   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\m_v7.wav"
#define VOICENAVstop   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\stop.wav"
#define VOICENAVALERN   "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\MapVoice\\camera.wav"


#define EMUTXTPATH  "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\EmuTxt\\"
#define EFFICTLOG "..\\ShareFolder\\MapData\\" // "\\" ##STORAGELOC "\\Setting\\elog.txt"


/*
 * Memory Management
 */

#define III_SIZE          III_UINT

#if defined(__cplusplus)
extern "C" {
#endif

III_VOID*                 III_malloc(III_SIZE );
III_VOID                  III_free(III_VOID *);

#ifdef HAVE_MEMORY_DETAIL_IMPLEMENTATION
III_VOID*                 III_memcpy(III_VOID *, const III_VOID *, III_SIZE);
III_VOID*                 III_memset(III_VOID *, III_INT, III_SIZE);
III_VOID                  III_bzero(III_VOID *, III_SIZE);
#endif

#if defined(__cplusplus)
}
#endif

/*
 * File Operations
 */


//#define III_FILE          FILE
#define III_FILE           void
#define III_SEEK_SET      0
#define III_SEEK_CUR      1
#define III_SEEK_END      2

#if defined(__cplusplus)
extern "C" {
#endif

III_FILE*                 III_fopen(const III_CHAR*, const III_CHAR*);
III_INT                   III_fclose(III_FILE *);
III_INT                   III_feof(III_FILE *);
III_INT                   III_fseek(III_FILE *, III_SIZE, III_INT );
III_SIZE                  III_fread(III_VOID*, III_SIZE , III_SIZE , III_FILE *);
III_SIZE                  III_fwrite(const III_VOID*, III_SIZE, III_SIZE, III_FILE *);

III_CHAR*                 III_fgets(III_CHAR *, III_SIZE, III_FILE *);
III_INT                   III_fputs(const III_CHAR *, III_FILE *);

#if defined(__cplusplus)
}
#endif

/*
 * Platform-depend functions
 */

#if defined(__cplusplus)
extern "C" {
#endif

III_VOID                  III_USleep(III_UINT); /* sleep resolution in microseconds */
III_VOID                  III_MSleep(III_UINT);  /* sleep resolution in milliseconds */
III_VOID                  III_SSleep(III_UINT);  /* sleep resolution in seconds */
III_VOID                  III_sleep(III_UCHAR, III_UINT); /* multi-functional sleep function portol */

#if defined(__cplusplus)
}
#endif

/*
 * Serial Communication functions
 */

#if defined(__cplusplus)

extern "C"
{

#endif

III_VOID*                III_SerialPort_Init ( III_VOID *, III_INT, III_CHAR, III_CHAR, III_CHAR );
//HANDLE					  III_SerialPort_Init ( TCHAR *, III_INT);
III_VOID                 III_SerialPort_Shutdown ( III_VOID * );
III_INT                III_SerialPort_ReadLine ( III_CHAR *, III_UINT );

#if defined(__cplusplus)

}

#endif

#endif/*_PORTING_H_*/
