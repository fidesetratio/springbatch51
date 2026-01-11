package com.app.tools.chunking;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerRecord {
	 private	long ledgerSeq;
	 private long accountId;
	 private BigDecimal amount;
	    
}
