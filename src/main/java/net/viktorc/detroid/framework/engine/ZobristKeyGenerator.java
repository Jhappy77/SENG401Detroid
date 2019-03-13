package net.viktorc.detroid.framework.engine;

import java.util.Random;
import net.viktorc.detroid.framework.util.BitOperations;

/**
 * A class for hashing chess positions. Two position objects with identical states will always have the same Zobrist keys within one runtime
 * and two position objects with different states (as defined by the side to move, the board state, the castling rights, and the en passant
 * rights) will almost always have different Zobrist keys.
 *
 * @author Viktor
 */
public class ZobristKeyGenerator {

  private static final long[] POLYGLOT_RANDOM_64 = {
      0x9D39247E33776D41L, 0x2AF7398005AAA5C7L, 0x44DB015024623547L, 0x9C15F73E62A76AE2L,
      0x75834465489C0C89L, 0x3290AC3A203001BFL, 0x0FBBAD1F61042279L, 0xE83A908FF2FB60CAL,
      0x0D7E765D58755C10L, 0x1A083822CEAFE02DL, 0x9605D5F0E25EC3B0L, 0xD021FF5CD13A2ED5L,
      0x40BDF15D4A672E32L, 0x011355146FD56395L, 0x5DB4832046F3D9E5L, 0x239F8B2D7FF719CCL,
      0x05D1A1AE85B49AA1L, 0x679F848F6E8FC971L, 0x7449BBFF801FED0BL, 0x7D11CDB1C3B7ADF0L,
      0x82C7709E781EB7CCL, 0xF3218F1C9510786CL, 0x331478F3AF51BBE6L, 0x4BB38DE5E7219443L,
      0xAA649C6EBCFD50FCL, 0x8DBD98A352AFD40BL, 0x87D2074B81D79217L, 0x19F3C751D3E92AE1L,
      0xB4AB30F062B19ABFL, 0x7B0500AC42047AC4L, 0xC9452CA81A09D85DL, 0x24AA6C514DA27500L,
      0x4C9F34427501B447L, 0x14A68FD73C910841L, 0xA71B9B83461CBD93L, 0x03488B95B0F1850FL,
      0x637B2B34FF93C040L, 0x09D1BC9A3DD90A94L, 0x3575668334A1DD3BL, 0x735E2B97A4C45A23L,
      0x18727070F1BD400BL, 0x1FCBACD259BF02E7L, 0xD310A7C2CE9B6555L, 0xBF983FE0FE5D8244L,
      0x9F74D14F7454A824L, 0x51EBDC4AB9BA3035L, 0x5C82C505DB9AB0FAL, 0xFCF7FE8A3430B241L,
      0x3253A729B9BA3DDEL, 0x8C74C368081B3075L, 0xB9BC6C87167C33E7L, 0x7EF48F2B83024E20L,
      0x11D505D4C351BD7FL, 0x6568FCA92C76A243L, 0x4DE0B0F40F32A7B8L, 0x96D693460CC37E5DL,
      0x42E240CB63689F2FL, 0x6D2BDCDAE2919661L, 0x42880B0236E4D951L, 0x5F0F4A5898171BB6L,
      0x39F890F579F92F88L, 0x93C5B5F47356388BL, 0x63DC359D8D231B78L, 0xEC16CA8AEA98AD76L,
      0x5355F900C2A82DC7L, 0x07FB9F855A997142L, 0x5093417AA8A7ED5EL, 0x7BCBC38DA25A7F3CL,
      0x19FC8A768CF4B6D4L, 0x637A7780DECFC0D9L, 0x8249A47AEE0E41F7L, 0x79AD695501E7D1E8L,
      0x14ACBAF4777D5776L, 0xF145B6BECCDEA195L, 0xDABF2AC8201752FCL, 0x24C3C94DF9C8D3F6L,
      0xBB6E2924F03912EAL, 0x0CE26C0B95C980D9L, 0xA49CD132BFBF7CC4L, 0xE99D662AF4243939L,
      0x27E6AD7891165C3FL, 0x8535F040B9744FF1L, 0x54B3F4FA5F40D873L, 0x72B12C32127FED2BL,
      0xEE954D3C7B411F47L, 0x9A85AC909A24EAA1L, 0x70AC4CD9F04F21F5L, 0xF9B89D3E99A075C2L,
      0x87B3E2B2B5C907B1L, 0xA366E5B8C54F48B8L, 0xAE4A9346CC3F7CF2L, 0x1920C04D47267BBDL,
      0x87BF02C6B49E2AE9L, 0x092237AC237F3859L, 0xFF07F64EF8ED14D0L, 0x8DE8DCA9F03CC54EL,
      0x9C1633264DB49C89L, 0xB3F22C3D0B0B38EDL, 0x390E5FB44D01144BL, 0x5BFEA5B4712768E9L,
      0x1E1032911FA78984L, 0x9A74ACB964E78CB3L, 0x4F80F7A035DAFB04L, 0x6304D09A0B3738C4L,
      0x2171E64683023A08L, 0x5B9B63EB9CEFF80CL, 0x506AACF489889342L, 0x1881AFC9A3A701D6L,
      0x6503080440750644L, 0xDFD395339CDBF4A7L, 0xEF927DBCF00C20F2L, 0x7B32F7D1E03680ECL,
      0xB9FD7620E7316243L, 0x05A7E8A57DB91B77L, 0xB5889C6E15630A75L, 0x4A750A09CE9573F7L,
      0xCF464CEC899A2F8AL, 0xF538639CE705B824L, 0x3C79A0FF5580EF7FL, 0xEDE6C87F8477609DL,
      0x799E81F05BC93F31L, 0x86536B8CF3428A8CL, 0x97D7374C60087B73L, 0xA246637CFF328532L,
      0x043FCAE60CC0EBA0L, 0x920E449535DD359EL, 0x70EB093B15B290CCL, 0x73A1921916591CBDL,
      0x56436C9FE1A1AA8DL, 0xEFAC4B70633B8F81L, 0xBB215798D45DF7AFL, 0x45F20042F24F1768L,
      0x930F80F4E8EB7462L, 0xFF6712FFCFD75EA1L, 0xAE623FD67468AA70L, 0xDD2C5BC84BC8D8FCL,
      0x7EED120D54CF2DD9L, 0x22FE545401165F1CL, 0xC91800E98FB99929L, 0x808BD68E6AC10365L,
      0xDEC468145B7605F6L, 0x1BEDE3A3AEF53302L, 0x43539603D6C55602L, 0xAA969B5C691CCB7AL,
      0xA87832D392EFEE56L, 0x65942C7B3C7E11AEL, 0xDED2D633CAD004F6L, 0x21F08570F420E565L,
      0xB415938D7DA94E3CL, 0x91B859E59ECB6350L, 0x10CFF333E0ED804AL, 0x28AED140BE0BB7DDL,
      0xC5CC1D89724FA456L, 0x5648F680F11A2741L, 0x2D255069F0B7DAB3L, 0x9BC5A38EF729ABD4L,
      0xEF2F054308F6A2BCL, 0xAF2042F5CC5C2858L, 0x480412BAB7F5BE2AL, 0xAEF3AF4A563DFE43L,
      0x19AFE59AE451497FL, 0x52593803DFF1E840L, 0xF4F076E65F2CE6F0L, 0x11379625747D5AF3L,
      0xBCE5D2248682C115L, 0x9DA4243DE836994FL, 0x066F70B33FE09017L, 0x4DC4DE189B671A1CL,
      0x51039AB7712457C3L, 0xC07A3F80C31FB4B4L, 0xB46EE9C5E64A6E7CL, 0xB3819A42ABE61C87L,
      0x21A007933A522A20L, 0x2DF16F761598AA4FL, 0x763C4A1371B368FDL, 0xF793C46702E086A0L,
      0xD7288E012AEB8D31L, 0xDE336A2A4BC1C44BL, 0x0BF692B38D079F23L, 0x2C604A7A177326B3L,
      0x4850E73E03EB6064L, 0xCFC447F1E53C8E1BL, 0xB05CA3F564268D99L, 0x9AE182C8BC9474E8L,
      0xA4FC4BD4FC5558CAL, 0xE755178D58FC4E76L, 0x69B97DB1A4C03DFEL, 0xF9B5B7C4ACC67C96L,
      0xFC6A82D64B8655FBL, 0x9C684CB6C4D24417L, 0x8EC97D2917456ED0L, 0x6703DF9D2924E97EL,
      0xC547F57E42A7444EL, 0x78E37644E7CAD29EL, 0xFE9A44E9362F05FAL, 0x08BD35CC38336615L,
      0x9315E5EB3A129ACEL, 0x94061B871E04DF75L, 0xDF1D9F9D784BA010L, 0x3BBA57B68871B59DL,
      0xD2B7ADEEDED1F73FL, 0xF7A255D83BC373F8L, 0xD7F4F2448C0CEB81L, 0xD95BE88CD210FFA7L,
      0x336F52F8FF4728E7L, 0xA74049DAC312AC71L, 0xA2F61BB6E437FDB5L, 0x4F2A5CB07F6A35B3L,
      0x87D380BDA5BF7859L, 0x16B9F7E06C453A21L, 0x7BA2484C8A0FD54EL, 0xF3A678CAD9A2E38CL,
      0x39B0BF7DDE437BA2L, 0xFCAF55C1BF8A4424L, 0x18FCF680573FA594L, 0x4C0563B89F495AC3L,
      0x40E087931A00930DL, 0x8CFFA9412EB642C1L, 0x68CA39053261169FL, 0x7A1EE967D27579E2L,
      0x9D1D60E5076F5B6FL, 0x3810E399B6F65BA2L, 0x32095B6D4AB5F9B1L, 0x35CAB62109DD038AL,
      0xA90B24499FCFAFB1L, 0x77A225A07CC2C6BDL, 0x513E5E634C70E331L, 0x4361C0CA3F692F12L,
      0xD941ACA44B20A45BL, 0x528F7C8602C5807BL, 0x52AB92BEB9613989L, 0x9D1DFA2EFC557F73L,
      0x722FF175F572C348L, 0x1D1260A51107FE97L, 0x7A249A57EC0C9BA2L, 0x04208FE9E8F7F2D6L,
      0x5A110C6058B920A0L, 0x0CD9A497658A5698L, 0x56FD23C8F9715A4CL, 0x284C847B9D887AAEL,
      0x04FEABFBBDB619CBL, 0x742E1E651C60BA83L, 0x9A9632E65904AD3CL, 0x881B82A13B51B9E2L,
      0x506E6744CD974924L, 0xB0183DB56FFC6A79L, 0x0ED9B915C66ED37EL, 0x5E11E86D5873D484L,
      0xF678647E3519AC6EL, 0x1B85D488D0F20CC5L, 0xDAB9FE6525D89021L, 0x0D151D86ADB73615L,
      0xA865A54EDCC0F019L, 0x93C42566AEF98FFBL, 0x99E7AFEABE000731L, 0x48CBFF086DDF285AL,
      0x7F9B6AF1EBF78BAFL, 0x58627E1A149BBA21L, 0x2CD16E2ABD791E33L, 0xD363EFF5F0977996L,
      0x0CE2A38C344A6EEDL, 0x1A804AADB9CFA741L, 0x907F30421D78C5DEL, 0x501F65EDB3034D07L,
      0x37624AE5A48FA6E9L, 0x957BAF61700CFF4EL, 0x3A6C27934E31188AL, 0xD49503536ABCA345L,
      0x088E049589C432E0L, 0xF943AEE7FEBF21B8L, 0x6C3B8E3E336139D3L, 0x364F6FFA464EE52EL,
      0xD60F6DCEDC314222L, 0x56963B0DCA418FC0L, 0x16F50EDF91E513AFL, 0xEF1955914B609F93L,
      0x565601C0364E3228L, 0xECB53939887E8175L, 0xBAC7A9A18531294BL, 0xB344C470397BBA52L,
      0x65D34954DAF3CEBDL, 0xB4B81B3FA97511E2L, 0xB422061193D6F6A7L, 0x071582401C38434DL,
      0x7A13F18BBEDC4FF5L, 0xBC4097B116C524D2L, 0x59B97885E2F2EA28L, 0x99170A5DC3115544L,
      0x6F423357E7C6A9F9L, 0x325928EE6E6F8794L, 0xD0E4366228B03343L, 0x565C31F7DE89EA27L,
      0x30F5611484119414L, 0xD873DB391292ED4FL, 0x7BD94E1D8E17DEBCL, 0xC7D9F16864A76E94L,
      0x947AE053EE56E63CL, 0xC8C93882F9475F5FL, 0x3A9BF55BA91F81CAL, 0xD9A11FBB3D9808E4L,
      0x0FD22063EDC29FCAL, 0xB3F256D8ACA0B0B9L, 0xB03031A8B4516E84L, 0x35DD37D5871448AFL,
      0xE9F6082B05542E4EL, 0xEBFAFA33D7254B59L, 0x9255ABB50D532280L, 0xB9AB4CE57F2D34F3L,
      0x693501D628297551L, 0xC62C58F97DD949BFL, 0xCD454F8F19C5126AL, 0xBBE83F4ECC2BDECBL,
      0xDC842B7E2819E230L, 0xBA89142E007503B8L, 0xA3BC941D0A5061CBL, 0xE9F6760E32CD8021L,
      0x09C7E552BC76492FL, 0x852F54934DA55CC9L, 0x8107FCCF064FCF56L, 0x098954D51FFF6580L,
      0x23B70EDB1955C4BFL, 0xC330DE426430F69DL, 0x4715ED43E8A45C0AL, 0xA8D7E4DAB780A08DL,
      0x0572B974F03CE0BBL, 0xB57D2E985E1419C7L, 0xE8D9ECBE2CF3D73FL, 0x2FE4B17170E59750L,
      0x11317BA87905E790L, 0x7FBF21EC8A1F45ECL, 0x1725CABFCB045B00L, 0x964E915CD5E2B207L,
      0x3E2B8BCBF016D66DL, 0xBE7444E39328A0ACL, 0xF85B2B4FBCDE44B7L, 0x49353FEA39BA63B1L,
      0x1DD01AAFCD53486AL, 0x1FCA8A92FD719F85L, 0xFC7C95D827357AFAL, 0x18A6A990C8B35EBDL,
      0xCCCB7005C6B9C28DL, 0x3BDBB92C43B17F26L, 0xAA70B5B4F89695A2L, 0xE94C39A54A98307FL,
      0xB7A0B174CFF6F36EL, 0xD4DBA84729AF48ADL, 0x2E18BC1AD9704A68L, 0x2DE0966DAF2F8B1CL,
      0xB9C11D5B1E43A07EL, 0x64972D68DEE33360L, 0x94628D38D0C20584L, 0xDBC0D2B6AB90A559L,
      0xD2733C4335C6A72FL, 0x7E75D99D94A70F4DL, 0x6CED1983376FA72BL, 0x97FCAACBF030BC24L,
      0x7B77497B32503B12L, 0x8547EDDFB81CCB94L, 0x79999CDFF70902CBL, 0xCFFE1939438E9B24L,
      0x829626E3892D95D7L, 0x92FAE24291F2B3F1L, 0x63E22C147B9C3403L, 0xC678B6D860284A1CL,
      0x5873888850659AE7L, 0x0981DCD296A8736DL, 0x9F65789A6509A440L, 0x9FF38FED72E9052FL,
      0xE479EE5B9930578CL, 0xE7F28ECD2D49EECDL, 0x56C074A581EA17FEL, 0x5544F7D774B14AEFL,
      0x7B3F0195FC6F290FL, 0x12153635B2C0CF57L, 0x7F5126DBBA5E0CA7L, 0x7A76956C3EAFB413L,
      0x3D5774A11D31AB39L, 0x8A1B083821F40CB4L, 0x7B4A38E32537DF62L, 0x950113646D1D6E03L,
      0x4DA8979A0041E8A9L, 0x3BC36E078F7515D7L, 0x5D0A12F27AD310D1L, 0x7F9D1A2E1EBE1327L,
      0xDA3A361B1C5157B1L, 0xDCDD7D20903D0C25L, 0x36833336D068F707L, 0xCE68341F79893389L,
      0xAB9090168DD05F34L, 0x43954B3252DC25E5L, 0xB438C2B67F98E5E9L, 0x10DCD78E3851A492L,
      0xDBC27AB5447822BFL, 0x9B3CDB65F82CA382L, 0xB67B7896167B4C84L, 0xBFCED1B0048EAC50L,
      0xA9119B60369FFEBDL, 0x1FFF7AC80904BF45L, 0xAC12FB171817EEE7L, 0xAF08DA9177DDA93DL,
      0x1B0CAB936E65C744L, 0xB559EB1D04E5E932L, 0xC37B45B3F8D6F2BAL, 0xC3A9DC228CAAC9E9L,
      0xF3B8B6675A6507FFL, 0x9FC477DE4ED681DAL, 0x67378D8ECCEF96CBL, 0x6DD856D94D259236L,
      0xA319CE15B0B4DB31L, 0x073973751F12DD5EL, 0x8A8E849EB32781A5L, 0xE1925C71285279F5L,
      0x74C04BF1790C0EFEL, 0x4DDA48153C94938AL, 0x9D266D6A1CC0542CL, 0x7440FB816508C4FEL,
      0x13328503DF48229FL, 0xD6BF7BAEE43CAC40L, 0x4838D65F6EF6748FL, 0x1E152328F3318DEAL,
      0x8F8419A348F296BFL, 0x72C8834A5957B511L, 0xD7A023A73260B45CL, 0x94EBC8ABCFB56DAEL,
      0x9FC10D0F989993E0L, 0xDE68A2355B93CAE6L, 0xA44CFE79AE538BBEL, 0x9D1D84FCCE371425L,
      0x51D2B1AB2DDFB636L, 0x2FD7E4B9E72CD38CL, 0x65CA5B96B7552210L, 0xDD69A0D8AB3B546DL,
      0x604D51B25FBF70E2L, 0x73AA8A564FB7AC9EL, 0x1A8C1E992B941148L, 0xAAC40A2703D9BEA0L,
      0x764DBEAE7FA4F3A6L, 0x1E99B96E70A9BE8BL, 0x2C5E9DEB57EF4743L, 0x3A938FEE32D29981L,
      0x26E6DB8FFDF5ADFEL, 0x469356C504EC9F9DL, 0xC8763C5B08D1908CL, 0x3F6C6AF859D80055L,
      0x7F7CC39420A3A545L, 0x9BFB227EBDF4C5CEL, 0x89039D79D6FC5C5CL, 0x8FE88B57305E2AB6L,
      0xA09E8C8C35AB96DEL, 0xFA7E393983325753L, 0xD6B6D0ECC617C699L, 0xDFEA21EA9E7557E3L,
      0xB67C1FA481680AF8L, 0xCA1E3785A9E724E5L, 0x1CFC8BED0D681639L, 0xD18D8549D140CAEAL,
      0x4ED0FE7E9DC91335L, 0xE4DBF0634473F5D2L, 0x1761F93A44D5AEFEL, 0x53898E4C3910DA55L,
      0x734DE8181F6EC39AL, 0x2680B122BAA28D97L, 0x298AF231C85BAFABL, 0x7983EED3740847D5L,
      0x66C1A2A1A60CD889L, 0x9E17E49642A3E4C1L, 0xEDB454E7BADC0805L, 0x50B704CAB602C329L,
      0x4CC317FB9CDDD023L, 0x66B4835D9EAFEA22L, 0x219B97E26FFC81BDL, 0x261E4E4C0A333A9DL,
      0x1FE2CCA76517DB90L, 0xD7504DFA8816EDBBL, 0xB9571FA04DC089C8L, 0x1DDC0325259B27DEL,
      0xCF3F4688801EB9AAL, 0xF4F5D05C10CAB243L, 0x38B6525C21A42B0EL, 0x36F60E2BA4FA6800L,
      0xEB3593803173E0CEL, 0x9C4CD6257C5A3603L, 0xAF0C317D32ADAA8AL, 0x258E5A80C7204C4BL,
      0x8B889D624D44885DL, 0xF4D14597E660F855L, 0xD4347F66EC8941C3L, 0xE699ED85B0DFB40DL,
      0x2472F6207C2D0484L, 0xC2A1E7B5B459AEB5L, 0xAB4F6451CC1D45ECL, 0x63767572AE3D6174L,
      0xA59E0BD101731A28L, 0x116D0016CB948F09L, 0x2CF9C8CA052F6E9FL, 0x0B090A7560A968E3L,
      0xABEEDDB2DDE06FF1L, 0x58EFC10B06A2068DL, 0xC6E57A78FBD986E0L, 0x2EAB8CA63CE802D7L,
      0x14A195640116F336L, 0x7C0828DD624EC390L, 0xD74BBE77E6116AC7L, 0x804456AF10F5FB53L,
      0xEBE9EA2ADF4321C7L, 0x03219A39EE587A30L, 0x49787FEF17AF9924L, 0xA1E9300CD8520548L,
      0x5B45E522E4B1B4EFL, 0xB49C3B3995091A36L, 0xD4490AD526F14431L, 0x12A8F216AF9418C2L,
      0x001F837CC7350524L, 0x1877B51E57A764D5L, 0xA2853B80F17F58EEL, 0x993E1DE72D36D310L,
      0xB3598080CE64A656L, 0x252F59CF0D9F04BBL, 0xD23C8E176D113600L, 0x1BDA0492E7E4586EL,
      0x21E0BD5026C619BFL, 0x3B097ADAF088F94EL, 0x8D14DEDB30BE846EL, 0xF95CFFA23AF5F6F4L,
      0x3871700761B3F743L, 0xCA672B91E9E4FA16L, 0x64C8E531BFF53B55L, 0x241260ED4AD1E87DL,
      0x106C09B972D2E822L, 0x7FBA195410E5CA30L, 0x7884D9BC6CB569D8L, 0x0647DFEDCD894A29L,
      0x63573FF03E224774L, 0x4FC8E9560F91B123L, 0x1DB956E450275779L, 0xB8D91274B9E9D4FBL,
      0xA2EBEE47E2FBFCE1L, 0xD9F1F30CCD97FB09L, 0xEFED53D75FD64E6BL, 0x2E6D02C36017F67FL,
      0xA9AA4D20DB084E9BL, 0xB64BE8D8B25396C1L, 0x70CB6AF7C2D5BCF0L, 0x98F076A4F7A2322EL,
      0xBF84470805E69B5FL, 0x94C3251F06F90CF3L, 0x3E003E616A6591E9L, 0xB925A6CD0421AFF3L,
      0x61BDD1307C66E300L, 0xBF8D5108E27E0D48L, 0x240AB57A8B888B20L, 0xFC87614BAF287E07L,
      0xEF02CDD06FFDB432L, 0xA1082C0466DF6C0AL, 0x8215E577001332C8L, 0xD39BB9C3A48DB6CFL,
      0x2738259634305C14L, 0x61CF4F94C97DF93DL, 0x1B6BACA2AE4E125BL, 0x758F450C88572E0BL,
      0x959F587D507A8359L, 0xB063E962E045F54DL, 0x60E8ED72C0DFF5D1L, 0x7B64978555326F9FL,
      0xFD080D236DA814BAL, 0x8C90FD9B083F4558L, 0x106F72FE81E2C590L, 0x7976033A39F7D952L,
      0xA4EC0132764CA04BL, 0x733EA705FAE4FA77L, 0xB4D8F77BC3E56167L, 0x9E21F4F903B33FD9L,
      0x9D765E419FB69F6DL, 0xD30C088BA61EA5EFL, 0x5D94337FBFAF7F5BL, 0x1A4E4822EB4D7A59L,
      0x6FFE73E81B637FB3L, 0xDDF957BC36D8B9CAL, 0x64D0E29EEA8838B3L, 0x08DD9BDFD96B9F63L,
      0x087E79E5A57D1D13L, 0xE328E230E3E2B3FBL, 0x1C2559E30F0946BEL, 0x720BF5F26F4D2EAAL,
      0xB0774D261CC609DBL, 0x443F64EC5A371195L, 0x4112CF68649A260EL, 0xD813F2FAB7F5C5CAL,
      0x660D3257380841EEL, 0x59AC2C7873F910A3L, 0xE846963877671A17L, 0x93B633ABFA3469F8L,
      0xC0C0F5A60EF4CDCFL, 0xCAF21ECD4377B28CL, 0x57277707199B8175L, 0x506C11B9D90E8B1DL,
      0xD83CC2687A19255FL, 0x4A29C6465A314CD1L, 0xED2DF21216235097L, 0xB5635C95FF7296E2L,
      0x22AF003AB672E811L, 0x52E762596BF68235L, 0x9AEBA33AC6ECC6B0L, 0x944F6DE09134DFB6L,
      0x6C47BEC883A7DE39L, 0x6AD047C430A12104L, 0xA5B1CFDBA0AB4067L, 0x7C45D833AFF07862L,
      0x5092EF950A16DA0BL, 0x9338E69C052B8E7BL, 0x455A4B4CFE30E3F5L, 0x6B02E63195AD0CF8L,
      0x6B17B224BAD6BF27L, 0xD1E0CCD25BB9C169L, 0xDE0C89A556B9AE70L, 0x50065E535A213CF6L,
      0x9C1169FA2777B874L, 0x78EDEFD694AF1EEDL, 0x6DC93D9526A50E68L, 0xEE97F453F06791EDL,
      0x32AB0EDB696703D3L, 0x3A6853C7E70757A7L, 0x31865CED6120F37DL, 0x67FEF95D92607890L,
      0x1F2B1D1F15F6DC9CL, 0xB69E38A8965C6B65L, 0xAA9119FF184CCCF4L, 0xF43C732873F24C13L,
      0xFB4A3D794A9A80D2L, 0x3550C2321FD6109CL, 0x371F77E76BB8417EL, 0x6BFA9AAE5EC05779L,
      0xCD04F3FF001A4778L, 0xE3273522064480CAL, 0x9F91508BFFCFC14AL, 0x049A7F41061A9E60L,
      0xFCB6BE43A9F2FE9BL, 0x08DE8A1C7797DA9BL, 0x8F9887E6078735A1L, 0xB5B4071DBFC73A66L,
      0x230E343DFBA08D33L, 0x43ED7F5A0FAE657DL, 0x3A88A0FBBCB05C63L, 0x21874B8B4D2DBC4FL,
      0x1BDEA12E35F6A8C9L, 0x53C065C6C8E63528L, 0xE34A1D250E7A8D6BL, 0xD6B04D3B7651DD7EL,
      0x5E90277E7CB39E2DL, 0x2C046F22062DC67DL, 0xB10BB459132D0A26L, 0x3FA9DDFB67E2F199L,
      0x0E09B88E1914F7AFL, 0x10E8B35AF3EEAB37L, 0x9EEDECA8E272B933L, 0xD4C718BC4AE8AE5FL,
      0x81536D601170FC20L, 0x91B534F885818A06L, 0xEC8177F83F900978L, 0x190E714FADA5156EL,
      0xB592BF39B0364963L, 0x89C350C893AE7DC1L, 0xAC042E70F8B383F2L, 0xB49B52E587A1EE60L,
      0xFB152FE3FF26DA89L, 0x3E666E6F69AE2C15L, 0x3B544EBE544C19F9L, 0xE805A1E290CF2456L,
      0x24B33C9D7ED25117L, 0xE74733427B72F0C1L, 0x0A804D18B7097475L, 0x57E3306D881EDB4FL,
      0x4AE7D6A36EB5DBCBL, 0x2D8D5432157064C8L, 0xD1E649DE1E7F268BL, 0x8A328A1CEDFE552CL,
      0x07A3AEC79624C7DAL, 0x84547DDC3E203C94L, 0x990A98FD5071D263L, 0x1A4FF12616EEFC89L,
      0xF6F7FD1431714200L, 0x30C05B1BA332F41CL, 0x8D2636B81555A786L, 0x46C9FEB55D120902L,
      0xCCEC0A73B49C9921L, 0x4E9D2827355FC492L, 0x19EBB029435DCB0FL, 0x4659D2B743848A2CL,
      0x963EF2C96B33BE31L, 0x74F85198B05A2E7DL, 0x5A0F544DD2B1FB18L, 0x03727073C2E134B1L,
      0xC7F6AA2DE59AEA61L, 0x352787BAA0D7C22FL, 0x9853EAB63B5E0B35L, 0xABBDCDD7ED5C0860L,
      0xCF05DAF5AC8D77B0L, 0x49CAD48CEBF4A71EL, 0x7A4C10EC2158C4A6L, 0xD9E92AA246BF719EL,
      0x13AE978D09FE5557L, 0x730499AF921549FFL, 0x4E4B705B92903BA4L, 0xFF577222C14F0A3AL,
      0x55B6344CF97AAFAEL, 0xB862225B055B6960L, 0xCAC09AFBDDD2CDB4L, 0xDAF8E9829FE96B5FL,
      0xB5FDFC5D3132C498L, 0x310CB380DB6F7503L, 0xE87FBB46217A360EL, 0x2102AE466EBB1148L,
      0xF8549E1A3AA5E00DL, 0x07A69AFDCC42261AL, 0xC4C118BFE78FEAAEL, 0xF9F4892ED96BD438L,
      0x1AF3DBE25D8F45DAL, 0xF5B4B0B0D2DEEEB4L, 0x962ACEEFA82E1C84L, 0x046E3ECAAF453CE9L,
      0xF05D129681949A4CL, 0x964781CE734B3C84L, 0x9C2ED44081CE5FBDL, 0x522E23F3925E319EL,
      0x177E00F9FC32F791L, 0x2BC60A63A6F3B3F2L, 0x222BBFAE61725606L, 0x486289DDCC3D6780L,
      0x7DC7785B8EFDFC80L, 0x8AF38731C02BA980L, 0x1FAB64EA29A2DDF7L, 0xE4D9429322CD065AL,
      0x9DA058C67844F20CL, 0x24C0E332B70019B0L, 0x233003B5A6CFE6ADL, 0xD586BD01C5C217F6L,
      0x5E5637885F29BC2BL, 0x7EBA726D8C94094BL, 0x0A56A5F0BFE39272L, 0xD79476A84EE20D06L,
      0x9E4C1269BAA4BF37L, 0x17EFEE45B0DEE640L, 0x1D95B0A5FCF90BC6L, 0x93CBE0B699C2585DL,
      0x65FA4F227A2B6D79L, 0xD5F9E858292504D5L, 0xC2B5A03F71471A6FL, 0x59300222B4561E00L,
      0xCE2F8642CA0712DCL, 0x7CA9723FBB2E8988L, 0x2785338347F2BA08L, 0xC61BB3A141E50E8CL,
      0x150F361DAB9DEC26L, 0x9F6A419D382595F4L, 0x64A53DC924FE7AC9L, 0x142DE49FFF7A7C3DL,
      0x0C335248857FA9E7L, 0x0A9C32D5EAE45305L, 0xE6C42178C4BBB92EL, 0x71F1CE2490D20B07L,
      0xF1BCC3D275AFE51AL, 0xE728E8C83C334074L, 0x96FBF83A12884624L, 0x81A1549FD6573DA5L,
      0x5FA7867CAF35E149L, 0x56986E2EF3ED091BL, 0x917F1DD5F8886C61L, 0xD20D8C88C8FFE65FL,
      0x31D71DCE64B2C310L, 0xF165B587DF898190L, 0xA57E6339DD2CF3A0L, 0x1EF6E6DBB1961EC9L,
      0x70CC73D90BC26E24L, 0xE21A6B35DF0C3AD7L, 0x003A93D8B2806962L, 0x1C99DED33CB890A1L,
      0xCF3145DE0ADD4289L, 0xD0E4427A5514FB72L, 0x77C621CC9FB3A483L, 0x67A34DAC4356550BL,
      0xF8D626AAAF278509L
  };

  private static final ZobristKeyGenerator INSTANCE = new ZobristKeyGenerator();

  private long turn;
  private long[][] board = new long[Piece.values().length][Bitboard.Square.values().length];
  private long[] whiteCastlingRights = new long[CastlingRights.values().length];
  private long[] blackCastlingRights = new long[CastlingRights.values().length];
  private long[] enPassantRights = new long[EnPassantRights.values().length];

  private ZobristKeyGenerator() {
    Random random = new Random();
    turn = random.nextLong();
    for (int i = 0; i < board[0].length; i++) {
      board[0][i] = 0;
    }
    for (int i = 1; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        board[i][j] = random.nextLong();
      }
    }
    for (int i = 0; i < whiteCastlingRights.length; i++) {
      whiteCastlingRights[i] = random.nextLong();
    }
    for (int i = 0; i < blackCastlingRights.length; i++) {
      blackCastlingRights[i] = random.nextLong();
    }
    for (int i = 0; i < enPassantRights.length; i++) {
      enPassantRights[i] = random.nextLong();
    }
  }

  /**
   * @return The one and only instance.
   */
  public static ZobristKeyGenerator getInstance() {
    return INSTANCE;
  }

  /**
   * Generates a main Zobrist hash key for the position based on the side to move, the board state, and the castling and en passant rights.
   *
   * @param pos The position to hash.
   * @return The 64 bit Zobrist hash key.
   */
  public long generateHashKey(Position pos) {
    long key = 0;
    if (!pos.isWhitesTurn()) {
      key ^= turn;
    }
    for (int i = 0; i < board.length; i++) {
      key ^= board[pos.getPiece(i)][i];
    }
    key ^= whiteCastlingRights[pos.getWhiteCastlingRights()];
    key ^= blackCastlingRights[pos.getBlackCastlingRights()];
    key ^= enPassantRights[pos.getEnPassantRights()];
    return key;
  }

  /**
   * Generates a Zobrist hash key for the pawn-king structure on the board.
   *
   * @param pos The position whose pawn-king structure is to be hashed.
   * @return The 64 bit pawn-king Zobrist hash key.
   */
  public long generatePawnKingHashKey(Position pos) {
    long key = 0;
    long whitePawns = pos.getWhitePawns();
    long blackPawns = pos.getBlackPawns();
    while (whitePawns != 0) {
      key ^= board[Piece.W_PAWN.ordinal()][BitOperations.indexOfLSBit(whitePawns)];
      whitePawns = BitOperations.resetLSBit(whitePawns);
    }
    while (blackPawns != 0) {
      key ^= board[Piece.B_PAWN.ordinal()][BitOperations.indexOfLSBit(blackPawns)];
      blackPawns = BitOperations.resetLSBit(blackPawns);
    }
    key ^= board[Piece.W_KING.ordinal()][BitOperations.indexOfBit(pos.getWhiteKing())];
    key ^= board[Piece.B_KING.ordinal()][BitOperations.indexOfBit(pos.getBlackKing())];
    return key;
  }

  /**
   * Generates a 64 bitboard hash key used for positions in PolyGlot opening books.
   *
   * @param pos The position whose PolyGlot hash key is to be generated.
   * @return The PolyGlot hash key of the position.
   */
  public long generatePolyglotHashKey(Position pos) {
    long key = 0L;
    int wCastlingRights = pos.getWhiteCastlingRights();
    int bCastlingRights = pos.getBlackCastlingRights();
    int enPassantRights = pos.getEnPassantRights();
    boolean whitesTurn = pos.isWhitesTurn();
    for (int i = 0; i < board.length; i++) {
      int pieceNote;
      int piece = pos.getPiece(i);
      if (piece == Piece.NULL.ordinal()) {
        continue;
      } else if (piece == Piece.B_PAWN.ordinal()) {
        pieceNote = 0;
      } else if (piece == Piece.W_PAWN.ordinal()) {
        pieceNote = 1;
      } else if (piece == Piece.B_KNIGHT.ordinal()) {
        pieceNote = 2;
      } else if (piece == Piece.W_KNIGHT.ordinal()) {
        pieceNote = 3;
      } else if (piece == Piece.B_BISHOP.ordinal()) {
        pieceNote = 4;
      } else if (piece == Piece.W_BISHOP.ordinal()) {
        pieceNote = 5;
      } else if (piece == Piece.B_ROOK.ordinal()) {
        pieceNote = 6;
      } else if (piece == Piece.W_ROOK.ordinal()) {
        pieceNote = 7;
      } else if (piece == Piece.B_QUEEN.ordinal()) {
        pieceNote = 8;
      } else if (piece == Piece.W_QUEEN.ordinal()) {
        pieceNote = 9;
      } else if (piece == Piece.B_KING.ordinal()) {
        pieceNote = 10;
      } else {
        pieceNote = 11;
      }
      key ^= POLYGLOT_RANDOM_64[64 * pieceNote + 8 * (i / 8) + i % 8];
    }
    if (wCastlingRights == CastlingRights.ALL.ordinal()) {
      key ^= POLYGLOT_RANDOM_64[768] ^ POLYGLOT_RANDOM_64[769];
    } else if (wCastlingRights == CastlingRights.SHORT.ordinal()) {
      key ^= POLYGLOT_RANDOM_64[768];
    } else if (wCastlingRights == CastlingRights.LONG.ordinal()) {
      key ^= POLYGLOT_RANDOM_64[769];
    }
    if (bCastlingRights == CastlingRights.ALL.ordinal()) {
      key ^= POLYGLOT_RANDOM_64[770] ^ POLYGLOT_RANDOM_64[771];
    } else if (bCastlingRights == CastlingRights.SHORT.ordinal()) {
      key ^= POLYGLOT_RANDOM_64[770];
    } else if (bCastlingRights == CastlingRights.LONG.ordinal()) {
      key ^= POLYGLOT_RANDOM_64[771];
    }
    if (enPassantRights != EnPassantRights.NONE.ordinal()) {
      if (whitesTurn) {
        MoveSetBase mD = MoveSetBase.values()[enPassantRights + EnPassantRights.TO_W_DEST_SQR_IND];
        if (mD.getBlackPawnCaptureSet(pos.getWhitePawns()) != 0) {
          key ^= POLYGLOT_RANDOM_64[772 + enPassantRights];
        }
      } else {
        MoveSetBase mD = MoveSetBase.values()[enPassantRights + EnPassantRights.TO_B_DEST_SQR_IND];
        if (mD.getWhitePawnCaptureSet(pos.getBlackPawns()) != 0) {
          key ^= POLYGLOT_RANDOM_64[772 + enPassantRights];
        }
      }
    }
    if (whitesTurn) {
      key ^= POLYGLOT_RANDOM_64[780];
    }
    return key;
  }

  /**
   * It updates a position's hash key with off-board state information such as the side to move, castling rights, and en passant rights.
   *
   * @param key The key to be updated.
   * @param prevState The state record for the position's previous state.
   * @param whiteCastlingRights The current white castling rights.
   * @param blackCastlingRights The current black castling rights.
   * @param enPassantRights The current en passant rights.
   * @return The key including the off-board state information.
   */
  public long getUpdatedOffBoardHashKey(long key, PositionStateRecord prevState, byte whiteCastlingRights,
      byte blackCastlingRights, byte enPassantRights) {
    key ^= turn;
    key ^= this.whiteCastlingRights[prevState.getWhiteCastlingRights()];
    key ^= this.blackCastlingRights[prevState.getBlackCastlingRights()];
    key ^= this.enPassantRights[prevState.getEnPassantRights()];
    key ^= this.whiteCastlingRights[whiteCastlingRights];
    key ^= this.blackCastlingRights[blackCastlingRights];
    key ^= this.enPassantRights[enPassantRights];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a normal move.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param movedPiece The moved piece.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterNormalMove(long key, byte from, byte to, byte movedPiece,
      byte capturedPiece) {
    long[] movedRow = board[movedPiece];
    key ^= movedRow[from];
    key ^= board[capturedPiece][to];
    key ^= movedRow[to];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a short castling by white.
   *
   * @param key The key to be updated.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterWhiteShortCastlinglMove(long key) {
    long[] kingRow = board[Piece.W_KING.ordinal()];
    key ^= kingRow[Bitboard.Square.E1.ordinal()];
    key ^= kingRow[Bitboard.Square.G1.ordinal()];
    long[] rookRow = board[Piece.W_ROOK.ordinal()];
    key ^= rookRow[Bitboard.Square.H1.ordinal()];
    key ^= rookRow[Bitboard.Square.F1.ordinal()];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a short castling by black.
   *
   * @param key The key to be updated.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterBlackShortCastlinglMove(long key) {
    long[] kingRow = board[Piece.B_KING.ordinal()];
    key ^= kingRow[Bitboard.Square.E8.ordinal()];
    key ^= kingRow[Bitboard.Square.G8.ordinal()];
    long[] rookRow = board[Piece.B_ROOK.ordinal()];
    key ^= rookRow[Bitboard.Square.H8.ordinal()];
    key ^= rookRow[Bitboard.Square.F8.ordinal()];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a long castling by white.
   *
   * @param key The key to be updated.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterWhiteLongCastlinglMove(long key) {
    long[] kingRow = board[Piece.W_KING.ordinal()];
    key ^= kingRow[Bitboard.Square.E1.ordinal()];
    key ^= kingRow[Bitboard.Square.C1.ordinal()];
    long[] rookRow = board[Piece.W_ROOK.ordinal()];
    key ^= rookRow[Bitboard.Square.A1.ordinal()];
    key ^= rookRow[Bitboard.Square.D1.ordinal()];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a long castling by black.
   *
   * @param key The key to be updated.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterBlackLongCastlinglMove(long key) {
    long[] kingRow = board[Piece.B_KING.ordinal()];
    key ^= kingRow[Bitboard.Square.E8.ordinal()];
    key ^= kingRow[Bitboard.Square.C8.ordinal()];
    long[] rookRow = board[Piece.B_ROOK.ordinal()];
    key ^= rookRow[Bitboard.Square.A8.ordinal()];
    key ^= rookRow[Bitboard.Square.D8.ordinal()];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after an en passant by white.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterWhiteEnPassantMove(long key, byte from, byte to) {
    long[] whitePawnRow = board[Piece.W_PAWN.ordinal()];
    key ^= whitePawnRow[from];
    key ^= whitePawnRow[to];
    key ^= board[Piece.B_PAWN.ordinal()][to - 8];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after an en passant by black.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterBlackEnPassantMove(long key, byte from, byte to) {
    long[] blackPawnRow = board[Piece.B_PAWN.ordinal()];
    key ^= blackPawnRow[from];
    key ^= blackPawnRow[to];
    key ^= board[Piece.W_PAWN.ordinal()][to + 8];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a queen promotion by white.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterWhiteQueenPromotionMove(long key, byte from, byte to, byte capturedPiece) {
    key ^= board[Piece.W_PAWN.ordinal()][from];
    key ^= board[capturedPiece][to];
    key ^= board[Piece.W_QUEEN.ordinal()][to];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a queen promotion by black.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterBlackQueenPromotionMove(long key, byte from, byte to, byte capturedPiece) {
    key ^= board[Piece.B_PAWN.ordinal()][from];
    key ^= board[capturedPiece][to];
    key ^= board[Piece.B_QUEEN.ordinal()][to];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a rook promotion by white.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterWhiteRookPromotionMove(long key, byte from, byte to, byte capturedPiece) {
    key ^= board[Piece.W_PAWN.ordinal()][from];
    key ^= board[capturedPiece][to];
    key ^= board[Piece.W_ROOK.ordinal()][to];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a rook promotion by black.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterBlackRookPromotionMove(long key, byte from, byte to, byte capturedPiece) {
    key ^= board[Piece.B_PAWN.ordinal()][from];
    key ^= board[capturedPiece][to];
    key ^= board[Piece.B_ROOK.ordinal()][to];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a bishop promotion by white.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterWhiteBishopPromotionMove(long key, byte from, byte to, byte capturedPiece) {
    key ^= board[Piece.W_PAWN.ordinal()][from];
    key ^= board[capturedPiece][to];
    key ^= board[Piece.W_BISHOP.ordinal()][to];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a bishop promotion by black.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterBlackBishopPromotionMove(long key, byte from, byte to, byte capturedPiece) {
    key ^= board[Piece.B_PAWN.ordinal()][from];
    key ^= board[capturedPiece][to];
    key ^= board[Piece.B_BISHOP.ordinal()][to];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a knight promotion by white.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterWhiteKnightPromotionMove(long key, byte from, byte to, byte capturedPiece) {
    key ^= board[Piece.W_PAWN.ordinal()][from];
    key ^= board[capturedPiece][to];
    key ^= board[Piece.W_KNIGHT.ordinal()][to];
    return key;
  }

  /**
   * It updates a position's hash key with board state information after a knight promotion by black.
   *
   * @param key The key to be updated.
   * @param from The origin square of the move.
   * @param to The destination square of the move.
   * @param capturedPiece The captured piece.
   * @return The updated key.
   */
  public long getUpdatedBoardHashKeyAfterBlackKnightPromotionMove(long key, byte from, byte to, byte capturedPiece) {
    key ^= board[Piece.B_PAWN.ordinal()][from];
    key ^= board[capturedPiece][to];
    key ^= board[Piece.B_KNIGHT.ordinal()][to];
    return key;
  }

}
