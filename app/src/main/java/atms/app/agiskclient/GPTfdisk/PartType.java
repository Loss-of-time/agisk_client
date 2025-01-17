package atms.app.agiskclient.GPTfdisk;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import atms.app.agiskclient.Tools.TAG;

/**
 * Copied from below
 * // parttypes.cc
 * // Class to manage partition type codes -- a slight variant on MBR type
 * // codes, GUID type codes, and associated names.
 * <p>
 * /* This program is copyright (c) 2009-2018 by Roderick W. Smith. It is distributed
 * under the terms of the GNU GPL version 2, as detailed in the COPYING file.
 */

public class PartType {
    private static final List<PartType> supportedPartType = new LinkedList<>();

    public enum PartEnum {
        TYPE_FREESPACE,//This is just keep for agisk specific use,you can change it!
        TYPE_TODOPART
    }

    private static void AddType(int mbrType, String guidData, String name) {
        supportedPartType.add(new PartType(mbrType, guidData, name));
    }

    /**
     * Warning! At this time,I won't check if the filesystem is available on the device
     *
     * @param mbrType
     * @param guidData
     * @param name
     * @param available
     */
    private static void AddType(int mbrType, String guidData, String name, int available) {
        supportedPartType.add(new PartType(mbrType, guidData, name));
    }

    /**
     * I guess this is a time consuming process,so this work will be done just if needed
     */
    private static void initPartTypes() {
        if (supportedPartType.size() > 0) {
            return;
        }
        // Start with the "unused entry," which should normally appear only
        // on empty partition table entries....
        AddType(0x0000, "00000000-0000-0000-0000-000000000000", "Unused entry", 0);
        // DOS/Windows partition types, most of which are hidden from the "L" listing
        // (they're available mainly for MBR-to-GPT conversions).
        AddType(0x0100, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // FAT-12
        AddType(0x0400, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // FAT-16 < 32M
        AddType(0x0600, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // FAT-16
        AddType(0x0700, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 1); // NTFS (or HPFS)
        AddType(0x0b00, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // FAT-32
        AddType(0x0c00, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // FAT-32 LBA
        AddType(0x0c01, "E3C9E316-0B5C-4DB8-817D-F92DF00215AE", "Microsoft reserved");
        AddType(0x0e00, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // FAT-16 LBA
        AddType(0x1100, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // Hidden FAT-12
        AddType(0x1400, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // Hidden FAT-16 < 32M
        AddType(0x1600, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // Hidden FAT-16
        AddType(0x1700, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // Hidden NTFS (or HPFS)
        AddType(0x1b00, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // Hidden FAT-32
        AddType(0x1c00, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // Hidden FAT-32 LBA
        AddType(0x1e00, "EBD0A0A2-B9E5-4433-87C0-68B6B72699C7", "Microsoft basic data", 0); // Hidden FAT-16 LBA
        AddType(0x2700, "DE94BBA4-06D1-4D40-A16A-BFD50179D6AC", "Windows RE");
        // Open Network Install Environment (ONIE) specific types.
        // See http://www.onie.org/ and
        // https://github.com/opencomputeproject/onie/blob/master/patches/gptfdisk/add-onie-partition-types.patch
        AddType(0x3000, "7412F7D5-A156-4B13-81DC-867174929325", "ONIE boot");
        AddType(0x3001, "D4E6E2CD-4469-46F3-B5CB-1BFF57AFC149", "ONIE config");
        // Plan 9; see http://man.cat-v.org/9front/8/prep
        AddType(0x3900, "C91818F9-8025-47AF-89D2-F030D7000C2C", "Plan 9");
        // PowerPC reference platform boot partition
        AddType(0x4100, "9E1A2D38-C612-4316-AA26-8B49521E5A8B", "PowerPC PReP boot");
        // Windows LDM ("dynamic disk") types
        AddType(0x4200, "AF9B60A0-1431-4F62-BC68-3311714A69AD", "Windows LDM data"); // Logical disk manager
        AddType(0x4201, "5808C8AA-7E8F-42E0-85D2-E1E90434CFB3", "Windows LDM metadata"); // Logical disk manager
        AddType(0x4202, "E75CAF8F-F680-4CEE-AFA3-B001E56EFC2D", "Windows Storage Spaces"); // A newer LDM-type setup
        // An oddball IBM filesystem....
        AddType(0x7501, "37AFFC90-EF7D-4E96-91C3-2D7AE055B174", "IBM GPFS"); // General Parallel File System (GPFS)
        // ChromeOS-specific partition types...
        // Values taken from vboot_reference/firmware/lib/cgptlib/include/gpt.h in
        // ChromeOS source code, retrieved 12/23/2010. They're also at
        // http://www.chromium.org/chromium-os/chromiumos-design-docs/disk-format.
        // These have no MBR equivalents, AFAIK, so I'm using 0x7Fxx values, since they're close
        // to the Linux values.
        AddType(0x7f00, "FE3A2A5D-4F32-41A7-B725-ACCC3285A309", "ChromeOS kernel");
        AddType(0x7f01, "3CB8E202-3B7E-47DD-8A3C-7FF2A13CFCEC", "ChromeOS root");
        AddType(0x7f02, "2E0A753D-9E48-43B0-8337-B15192CB1B5E", "ChromeOS reserved");
        // Linux-specific partition types....
        AddType(0x8200, "0657FD6D-A4AB-43C4-84E5-0933C84B4F4F", "Linux swap"); // Linux swap (or Solaris on MBR)
        AddType(0x8300, "0FC63DAF-8483-4772-8E79-3D69D8477DE4", "Linux filesystem"); // Linux native
        AddType(0x8301, "8DA63339-0007-60C0-C436-083AC8230908", "Linux reserved");
        // See https://www.freedesktop.org/software/systemd/man/systemd-gpt-auto-generator.html
        // and https://systemd.io/DISCOVERABLE_PARTITIONS
        AddType(0x8302, "933AC7E1-2EB4-4F13-B844-0E14E2AEF915", "Linux /home"); // Linux /home (auto-mounted by systemd)
        AddType(0x8303, "44479540-F297-41B2-9AF7-D131D5F0458A", "Linux x86 root (/)"); // Linux / on x86 (auto-mounted by systemd)
        AddType(0x8304, "4F68BCE3-E8CD-4DB1-96E7-FBCAF984B709", "Linux x86-64 root (/)"); // Linux / on x86-64 (auto-mounted by systemd)
        AddType(0x8305, "B921B045-1DF0-41C3-AF44-4C6F280D3FAE", "Linux ARM64 root (/)"); // Linux / on 64-bit ARM (auto-mounted by systemd)
        AddType(0x8306, "3B8F8425-20E0-4F3B-907F-1A25A76F98E8", "Linux /srv"); // Linux /srv (auto-mounted by systemd)
        AddType(0x8307, "69DAD710-2CE4-4E3C-B16C-21A1D49ABED3", "Linux ARM32 root (/)"); // Linux / on 32-bit ARM (auto-mounted by systemd)
        AddType(0x8308, "7FFEC5C9-2D00-49B7-8941-3EA10A5586B7", "Linux dm-crypt");
        AddType(0x8309, "CA7D7CCB-63ED-4C53-861C-1742536059CC", "Linux LUKS");
        AddType(0x830A, "993D8D3D-F80E-4225-855A-9DAF8ED7EA97", "Linux IA-64 root (/)"); // Linux / on Itanium (auto-mounted by systemd)
        AddType(0x830B, "D13C5D3B-B5D1-422A-B29F-9454FDC89D76", "Linux x86 root verity");
        AddType(0x830C, "2C7357ED-EBD2-46D9-AEC1-23D437EC2BF5", "Linux x86-64 root verity");
        AddType(0x830D, "7386CDF2-203C-47A9-A498-F2ECCE45A2D6", "Linux ARM32 root verity");
        AddType(0x830E, "DF3300CE-D69F-4C92-978C-9BFB0F38D820", "Linux ARM64 root verity");
        AddType(0x830F, "86ED10D5-B607-45BB-8957-D350F23D0571", "Linux IA-64 root verity");
        AddType(0x8310, "4D21B016-B534-45C2-A9FB-5C16E091FD2D", "Linux /var"); // Linux /var (auto-mounted by systemd)
        AddType(0x8311, "7EC6F557-3BC5-4ACA-B293-16EF5DF639D1", "Linux /var/tmp"); // Linux /var/tmp (auto-mounted by systemd)
        // Used by Intel Rapid Start technology
        AddType(0x8400, "D3BFE2DE-3DAF-11DF-BA40-E3A556D89593", "Intel Rapid Start");
        // Another Linux type code....
        AddType(0x8e00, "E6D6D379-F507-44C2-A23C-238F2A3DF928", "Linux LVM");
        // Android type codes....
        // from Wikipedia, https://gist.github.com/culots/704afd126dec2f45c22d0c9d42cb7fab,
        // and my own Android devices' partition tables
        AddType(0xa000, "2568845D-2332-4675-BC39-8FA5A4748D15", "Android bootloader");
        AddType(0xa001, "114EAFFE-1552-4022-B26E-9B053604CF84", "Android bootloader 2");
        AddType(0xa002, "49A4D17F-93A3-45C1-A0DE-F50B2EBE2599", "Android boot 1");
        AddType(0xa003, "4177C722-9E92-4AAB-8644-43502BFD5506", "Android recovery 1");
        AddType(0xa004, "EF32A33B-A409-486C-9141-9FFB711F6266", "Android misc");
        AddType(0xa005, "20AC26BE-20B7-11E3-84C5-6CFDB94711E9", "Android metadata");
        AddType(0xa006, "38F428E6-D326-425D-9140-6E0EA133647C", "Android system 1");
        AddType(0xa007, "A893EF21-E428-470A-9E55-0668FD91A2D9", "Android cache");
        AddType(0xa008, "DC76DDA9-5AC1-491C-AF42-A82591580C0D", "Android data");
        AddType(0xa009, "EBC597D0-2053-4B15-8B64-E0AAC75F4DB1", "Android persistent");
        AddType(0xa00a, "8F68CC74-C5E5-48DA-BE91-A0C8C15E9C80", "Android factory");
        AddType(0xa00b, "767941D0-2085-11E3-AD3B-6CFDB94711E9", "Android fastboot/tertiary");
        AddType(0xa00c, "AC6D7924-EB71-4DF8-B48D-E267B27148FF", "Android OEM");
        AddType(0xa00d, "C5A0AEEC-13EA-11E5-A1B1-001E67CA0C3C", "Android vendor");
        AddType(0xa00e, "BD59408B-4514-490D-BF12-9878D963F378", "Android config");
        AddType(0xa00f, "9FDAA6EF-4B3F-40D2-BA8D-BFF16BFB887B", "Android factory (alt)");
        AddType(0xa010, "19A710A2-B3CA-11E4-B026-10604B889DCF", "Android meta");
        AddType(0xa011, "193D1EA4-B3CA-11E4-B075-10604B889DCF", "Android EXT");
        AddType(0xa012, "DEA0BA2C-CBDD-4805-B4F9-F428251C3E98", "Android SBL1");
        AddType(0xa013, "8C6B52AD-8A9E-4398-AD09-AE916E53AE2D", "Android SBL2");
        AddType(0xa014, "05E044DF-92F1-4325-B69E-374A82E97D6E", "Android SBL3");
        AddType(0xa015, "400FFDCD-22E0-47E7-9A23-F16ED9382388", "Android APPSBL");
        AddType(0xa016, "A053AA7F-40B8-4B1C-BA08-2F68AC71A4F4", "Android QSEE/tz");
        AddType(0xa017, "E1A6A689-0C8D-4CC6-B4E8-55A4320FBD8A", "Android QHEE/hyp");
        AddType(0xa018, "098DF793-D712-413D-9D4E-89D711772228", "Android RPM");
        AddType(0xa019, "D4E0D938-B7FA-48C1-9D21-BC5ED5C4B203", "Android WDOG debug/sdi");
        AddType(0xa01a, "20A0C19C-286A-42FA-9CE7-F64C3226A794", "Android DDR");
        AddType(0xa01b, "A19F205F-CCD8-4B6D-8F1E-2D9BC24CFFB1", "Android CDT");
        AddType(0xa01c, "66C9B323-F7FC-48B6-BF96-6F32E335A428", "Android RAM dump");
        AddType(0xa01d, "303E6AC3-AF15-4C54-9E9B-D9A8FBECF401", "Android SEC");
        AddType(0xa01e, "C00EEF24-7709-43D6-9799-DD2B411E7A3C", "Android PMIC");
        AddType(0xa01f, "82ACC91F-357C-4A68-9C8F-689E1B1A23A1", "Android misc 1");
        AddType(0xa020, "E2802D54-0545-E8A1-A1E8-C7A3E245ACD4", "Android misc 2");
        AddType(0xa021, "65ADDCF4-0C5C-4D9A-AC2D-D90B5CBFCD03", "Android device info");
        AddType(0xa022, "E6E98DA2-E22A-4D12-AB33-169E7DEAA507", "Android APDP");
        AddType(0xa023, "ED9E8101-05FA-46B7-82AA-8D58770D200B", "Android MSADP");
        AddType(0xa024, "11406F35-1173-4869-807B-27DF71802812", "Android DPO");
        AddType(0xa025, "9D72D4E4-9958-42DA-AC26-BEA7A90B0434", "Android recovery 2");
        AddType(0xa026, "6C95E238-E343-4BA8-B489-8681ED22AD0B", "Android persist");
        AddType(0xa027, "EBBEADAF-22C9-E33B-8F5D-0E81686A68CB", "Android modem ST1");
        AddType(0xa028, "0A288B1F-22C9-E33B-8F5D-0E81686A68CB", "Android modem ST2");
        AddType(0xa029, "57B90A16-22C9-E33B-8F5D-0E81686A68CB", "Android FSC");
        AddType(0xa02a, "638FF8E2-22C9-E33B-8F5D-0E81686A68CB", "Android FSG 1");
        AddType(0xa02b, "2013373E-1AC4-4131-BFD8-B6A7AC638772", "Android FSG 2");
        AddType(0xa02c, "2C86E742-745E-4FDD-BFD8-B6A7AC638772", "Android SSD");
        AddType(0xa02d, "DE7D4029-0F5B-41C8-AE7E-F6C023A02B33", "Android keystore");
        AddType(0xa02e, "323EF595-AF7A-4AFA-8060-97BE72841BB9", "Android encrypt");
        AddType(0xa02f, "45864011-CF89-46E6-A445-85262E065604", "Android EKSST");
        AddType(0xa030, "8ED8AE95-597F-4C8A-A5BD-A7FF8E4DFAA9", "Android RCT");
        AddType(0xa031, "DF24E5ED-8C96-4B86-B00B-79667DC6DE11", "Android spare1");
        AddType(0xa032, "7C29D3AD-78B9-452E-9DEB-D098D542F092", "Android spare2");
        AddType(0xa033, "379D107E-229E-499D-AD4F-61F5BCF87BD4", "Android spare3");
        AddType(0xa034, "0DEA65E5-A676-4CDF-823C-77568B577ED5", "Android spare4");
        AddType(0xa035, "4627AE27-CFEF-48A1-88FE-99C3509ADE26", "Android raw resources");
        AddType(0xa036, "20117F86-E985-4357-B9EE-374BC1D8487D", "Android boot 2");
        AddType(0xa037, "86A7CB80-84E1-408C-99AB-694F1A410FC7", "Android FOTA");
        AddType(0xa038, "97D7B011-54DA-4835-B3C4-917AD6E73D74", "Android system 2");
        AddType(0xa039, "5594C694-C871-4B5F-90B1-690A6F68E0F7", "Android cache");
        AddType(0xa03a, "1B81E7E6-F50D-419B-A739-2AEEF8DA3335", "Android user data");
        AddType(0xa03b, "98523EC6-90FE-4C67-B50A-0FC59ED6F56D", "LG (Android) advanced flasher");
        AddType(0xa03c, "2644BCC0-F36A-4792-9533-1738BED53EE3", "Android PG1FS");
        AddType(0xa03d, "DD7C91E9-38C9-45C5-8A12-4A80F7E14057", "Android PG2FS");
        AddType(0xa03e, "7696D5B6-43FD-4664-A228-C563C4A1E8CC", "Android board info");
        AddType(0xa03f, "0D802D54-058D-4A20-AD2D-C7A362CEACD4", "Android MFG");
        AddType(0xa040, "10A0C19C-516A-5444-5CE3-664C3226A794", "Android limits");
        // Atari TOS partition type
        AddType(0xa200, "734E5AFE-F61A-11E6-BC64-92361F002671", "Atari TOS basic data");
        // FreeBSD partition types....
        // Note: Rather than extract FreeBSD disklabel data, convert FreeBSD
        // partitions in-place, and let FreeBSD sort out the details....
        AddType(0xa500, "516E7CB4-6ECF-11D6-8FF8-00022D09712B", "FreeBSD disklabel");
        AddType(0xa501, "83BD6B9D-7F41-11DC-BE0B-001560B84F0F", "FreeBSD boot");
        AddType(0xa502, "516E7CB5-6ECF-11D6-8FF8-00022D09712B", "FreeBSD swap");
        AddType(0xa503, "516E7CB6-6ECF-11D6-8FF8-00022D09712B", "FreeBSD UFS");
        AddType(0xa504, "516E7CBA-6ECF-11D6-8FF8-00022D09712B", "FreeBSD ZFS");
        AddType(0xa505, "516E7CB8-6ECF-11D6-8FF8-00022D09712B", "FreeBSD Vinum/RAID");
        // Midnight BSD partition types....
        AddType(0xa580, "85D5E45A-237C-11E1-B4B3-E89A8F7FC3A7", "Midnight BSD data");
        AddType(0xa581, "85D5E45E-237C-11E1-B4B3-E89A8F7FC3A7", "Midnight BSD boot");
        AddType(0xa582, "85D5E45B-237C-11E1-B4B3-E89A8F7FC3A7", "Midnight BSD swap");
        AddType(0xa583, "0394Ef8B-237E-11E1-B4B3-E89A8F7FC3A7", "Midnight BSD UFS");
        AddType(0xa584, "85D5E45D-237C-11E1-B4B3-E89A8F7FC3A7", "Midnight BSD ZFS");
        AddType(0xa585, "85D5E45C-237C-11E1-B4B3-E89A8F7FC3A7", "Midnight BSD Vinum");
        // OpenBSD partition type....
        AddType(0xa600, "824CC7A0-36A8-11E3-890A-952519AD3F61", "OpenBSD disklabel");
        // A MacOS partition type, separated from others by NetBSD partition types...
        AddType(0xa800, "55465300-0000-11AA-AA11-00306543ECAC", "Apple UFS"); // Mac OS X
        // NetBSD partition types. Note that the main entry sets it up as a
        // FreeBSD disklabel. I'm not 100% certain this is the correct behavior.
        AddType(0xa900, "516E7CB4-6ECF-11D6-8FF8-00022D09712B", "FreeBSD disklabel", 0); // NetBSD disklabel
        AddType(0xa901, "49F48D32-B10E-11DC-B99B-0019D1879648", "NetBSD swap");
        AddType(0xa902, "49F48D5A-B10E-11DC-B99B-0019D1879648", "NetBSD FFS");
        AddType(0xa903, "49F48D82-B10E-11DC-B99B-0019D1879648", "NetBSD LFS");
        AddType(0xa904, "2DB519C4-B10F-11DC-B99B-0019D1879648", "NetBSD concatenated");
        AddType(0xa905, "2DB519EC-B10F-11DC-B99B-0019D1879648", "NetBSD encrypted");
        AddType(0xa906, "49F48DAA-B10E-11DC-B99B-0019D1879648", "NetBSD RAID");
        // Mac OS partition types (See also 0xa800, above)....
        AddType(0xab00, "426F6F74-0000-11AA-AA11-00306543ECAC", "Recovery HD");
        AddType(0xaf00, "48465300-0000-11AA-AA11-00306543ECAC", "Apple HFS/HFS+");
        AddType(0xaf01, "52414944-0000-11AA-AA11-00306543ECAC", "Apple RAID");
        AddType(0xaf02, "52414944-5F4F-11AA-AA11-00306543ECAC", "Apple RAID offline");
        AddType(0xaf03, "4C616265-6C00-11AA-AA11-00306543ECAC", "Apple label");
        AddType(0xaf04, "5265636F-7665-11AA-AA11-00306543ECAC", "AppleTV recovery");
        AddType(0xaf05, "53746F72-6167-11AA-AA11-00306543ECAC", "Apple Core Storage");
        AddType(0xaf06, "B6FA30DA-92D2-4A9A-96F1-871EC6486200", "Apple SoftRAID Status");
        AddType(0xaf07, "2E313465-19B9-463F-8126-8A7993773801", "Apple SoftRAID Scratch");
        AddType(0xaf08, "FA709C7E-65B1-4593-BFD5-E71D61DE9B02", "Apple SoftRAID Volume");
        AddType(0xaf09, "BBBA6DF5-F46F-4A89-8F59-8765B2727503", "Apple SoftRAID Cache");
        AddType(0xaf0a, "7C3457EF-0000-11AA-AA11-00306543ECAC", "Apple APFS");
        // QNX Power-Safe (QNX6)
        AddType(0xb300, "CEF5A9AD-73BC-4601-89F3-CDEEEEE321A1", "QNX6 Power-Safe");
        // Acronis Secure Zone
        AddType(0xbc00, "0311FC50-01CA-4725-AD77-9ADBB20ACE98", "Acronis Secure Zone");
        // Solaris partition types (one of which is shared with MacOS)
        AddType(0xbe00, "6A82CB45-1DD2-11B2-99A6-080020736631", "Solaris boot");
        AddType(0xbf00, "6A85CF4D-1DD2-11B2-99A6-080020736631", "Solaris root");
        AddType(0xbf01, "6A898CC3-1DD2-11B2-99A6-080020736631", "Solaris /usr & Mac ZFS"); // Solaris/MacOS
        AddType(0xbf02, "6A87C46F-1DD2-11B2-99A6-080020736631", "Solaris swap");
        AddType(0xbf03, "6A8B642B-1DD2-11B2-99A6-080020736631", "Solaris backup");
        AddType(0xbf04, "6A8EF2E9-1DD2-11B2-99A6-080020736631", "Solaris /var");
        AddType(0xbf05, "6A90BA39-1DD2-11B2-99A6-080020736631", "Solaris /home");
        AddType(0xbf06, "6A9283A5-1DD2-11B2-99A6-080020736631", "Solaris alternate sector");
        AddType(0xbf07, "6A945A3B-1DD2-11B2-99A6-080020736631", "Solaris Reserved 1");
        AddType(0xbf08, "6A9630D1-1DD2-11B2-99A6-080020736631", "Solaris Reserved 2");
        AddType(0xbf09, "6A980767-1DD2-11B2-99A6-080020736631", "Solaris Reserved 3");
        AddType(0xbf0a, "6A96237F-1DD2-11B2-99A6-080020736631", "Solaris Reserved 4");
        AddType(0xbf0b, "6A8D2AC7-1DD2-11B2-99A6-080020736631", "Solaris Reserved 5");
        // I can find no MBR equivalents for these, but they're on the
        // Wikipedia page for GPT, so here we go....
        AddType(0xc001, "75894C1E-3AEB-11D3-B7C1-7B03A0000000", "HP-UX data");
        AddType(0xc002, "E2A1E728-32E3-11D6-A682-7B03A0000000", "HP-UX service");
        // Open Network Install Environment (ONIE) partitions....
        AddType(0xe100, "7412F7D5-A156-4B13-81DC-867174929325", "ONIE boot");
        AddType(0xe101, "D4E6E2CD-4469-46F3-B5CB-1BFF57AFC149", "ONIE config");
        // See http://www.freedesktop.org/wiki/Specifications/BootLoaderSpec
        AddType(0xea00, "BC13C2FF-59E6-4262-A352-B275FD6F7172", "Freedesktop $BOOT");
        // Type code for Haiku; uses BeOS MBR code as hex code base
        AddType(0xeb00, "42465331-3BA3-10F1-802A-4861696B7521", "Haiku BFS");
        // Manufacturer-specific ESP-like partitions (in order in which they were added)
        AddType(0xed00, "F4019732-066E-4E12-8273-346C5641494F", "Sony system partition");
        AddType(0xed01, "BFBFAFE7-A34F-448A-9A5B-6213EB736C22", "Lenovo system partition");
        // EFI system and related partitions
        AddType(0xef00, "C12A7328-F81F-11D2-BA4B-00A0C93EC93B", "EFI system partition"); // Parted identifies these as having the "boot flag" set
        AddType(0xef01, "024DEE41-33E7-11D3-9D69-0008C781F39F", "MBR partition scheme"); // Used to nest MBR in GPT
        AddType(0xef02, "21686148-6449-6E6F-744E-656564454649", "BIOS boot partition"); // Used by GRUB
        // Ceph type codes; see https://github.com/ceph/ceph/blob/9bcc42a3e6b08521694b5c0228b2c6ed7b3d312e/src/ceph-disk#L76-L81
        // and Wikipedia
        AddType(0xf800, "4FBD7E29-9D25-41B8-AFD0-062C0CEFF05D", "Ceph OSD"); // Ceph Object Storage Daemon
        AddType(0xf801, "4FBD7E29-9D25-41B8-AFD0-5EC00CEFF05D", "Ceph dm-crypt OSD"); // Ceph Object Storage Daemon (encrypted)
        AddType(0xf802, "45B0969E-9B03-4F30-B4C6-B4B80CEFF106", "Ceph journal");
        AddType(0xf803, "45B0969E-9B03-4F30-B4C6-5EC00CEFF106", "Ceph dm-crypt journal");
        AddType(0xf804, "89C57F98-2FE5-4DC0-89C1-F3AD0CEFF2BE", "Ceph disk in creation");
        AddType(0xf805, "89C57F98-2FE5-4DC0-89C1-5EC00CEFF2BE", "Ceph dm-crypt disk in creation");
        AddType(0xf806, "CAFECAFE-9B03-4F30-B4C6-B4B80CEFF106", "Ceph block");
        AddType(0xf807, "30CD0809-C2B2-499C-8879-2D6B78529876", "Ceph block DB");
        AddType(0xf808, "5CE17FCE-4087-4169-B7FF-056CC58473F9", "Ceph block write-ahead log");
        AddType(0xf809, "FB3AABF9-D25F-47CC-BF5E-721D1816496B", "Ceph lockbox for dm-crypt keys");
        AddType(0xf80a, "4FBD7E29-8AE0-4982-BF9D-5A8D867AF560", "Ceph multipath OSD");
        AddType(0xf80b, "45B0969E-8AE0-4982-BF9D-5A8D867AF560", "Ceph multipath journal");
        AddType(0xf80c, "CAFECAFE-8AE0-4982-BF9D-5A8D867AF560", "Ceph multipath block 1");
        AddType(0xf80d, "7F4A666A-16F3-47A2-8445-152EF4D03F6C", "Ceph multipath block 2");
        AddType(0xf80e, "EC6D6385-E346-45DC-BE91-DA2A7C8B3261", "Ceph multipath block DB");
        AddType(0xf80f, "01B41E1B-002A-453C-9F17-88793989FF8F", "Ceph multipath block write-ahead log");
        AddType(0xf810, "CAFECAFE-9B03-4F30-B4C6-5EC00CEFF106", "Ceph dm-crypt block");
        AddType(0xf811, "93B0052D-02D9-4D8A-A43B-33A3EE4DFBC3", "Ceph dm-crypt block DB");
        AddType(0xf812, "306E8683-4FE2-4330-B7C0-00A917C16966", "Ceph dm-crypt block write-ahead log");
        AddType(0xf813, "45B0969E-9B03-4F30-B4C6-35865CEFF106", "Ceph dm-crypt LUKS journal");
        AddType(0xf814, "CAFECAFE-9B03-4F30-B4C6-35865CEFF106", "Ceph dm-crypt LUKS block");
        AddType(0xf815, "166418DA-C469-4022-ADF4-B30AFD37F176", "Ceph dm-crypt LUKS block DB");
        AddType(0xf816, "86A32090-3647-40B9-BBBD-38D8C573AA86", "Ceph dm-crypt LUKS block write-ahead log");
        AddType(0xf817, "4FBD7E29-9D25-41B8-AFD0-35865CEFF05D", "Ceph dm-crypt LUKS OSD");
        // VMWare ESX partition types codes
        AddType(0xfb00, "AA31E02A-400F-11DB-9590-000C2911D1B8", "VMWare VMFS");
        AddType(0xfb01, "9198EFFC-31C0-11DB-8F78-000C2911D1B8", "VMWare reserved");
        AddType(0xfc00, "9D275380-40AD-11DB-BF97-000C2911D1B8", "VMWare kcore crash protection");
        // A straggler Linux partition type....
        AddType(0xfd00, "A19D880F-05FC-4D3B-A006-743F0F84911E", "Linux RAID");
        // Note: DO NOT use the 0xffff code; that's reserved to indicate an
        // unknown GUID type code.
    }

    /**
     * Part type attribute
     */
    private PartEnum part_enum;
    //    int PartType::AddType(uint16_t mbrType, const char * guidData, const char * name,
//                          int toDisplay)
    private int mbrType = 0;
    private String guidData = "";
    private String name = "";

    public static PartType getPartType(String guidData) {
        initPartTypes();
        Log.d(TAG.PART_TYPE_TAG, "Given guidData: " + guidData);
        if (guidData.equals("*FREESPACE")) {
            PartType partType = new PartType();
            partType.part_enum = PartEnum.TYPE_FREESPACE;
            return partType;
        }
        for (PartType ppt : supportedPartType) {
            if (ppt.getGuidData().equals(guidData)) {
                return ppt;
            }
        }
        //unsupported part type
        PartType unknown = new PartType();
        unknown.part_enum = PartEnum.TYPE_TODOPART;
        unknown.setName("Unknown Partition");
        unknown.setMbrType(0xffff);
        unknown.setGuidData("");
        return unknown;
    }
    public static PartType getPartType(int code) {
        Log.d(TAG.PART_TYPE_TAG, "Given mbrCode: " + Integer.toHexString(code));
        initPartTypes();
        if (code==0) {
            PartType partType = new PartType();
            partType.part_enum = PartEnum.TYPE_FREESPACE;
            return partType;
        }
        for (PartType ppt : supportedPartType) {
            if (ppt.getMbrType()==code) {
                return ppt;
            }
        }
        //unsupported part type
        PartType unknown = new PartType();
        unknown.part_enum = PartEnum.TYPE_TODOPART;
        unknown.setName("Unknown Partition");
        unknown.setMbrType(0xffff);
        unknown.setGuidData("");
        return unknown;
    }

    private PartType() {

    }
    private PartType(int mbrcode, String uuid, String name) {
        this.part_enum = PartEnum.TYPE_TODOPART;
        this.mbrType = mbrcode;
        this.guidData = uuid;
        this.name = name;
    }

    public PartEnum getPartType() {
        return this.part_enum;
    }


    //TODO

    /**
     * Now just support ext4 (android)
     * I think this function is unnecessary
     * @return
     */
    public String getFilesystemName() {
        switch (this.mbrType) {
            case 0x8300:
                return "ext4";
        }
        return "TODO";
    }

    public String getName() {
        return name;
    }

    public int getMbrType() {
        return mbrType;
    }

    public String getGuidData() {
        return guidData;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGuidData(String guidData) {
        this.guidData = guidData;
    }

    public void setMbrType(int mbrType) {
        this.mbrType = mbrType;
    }

    public static List<PartType> getSupportedPartType() {
        initPartTypes();
        return supportedPartType;
    }
}
