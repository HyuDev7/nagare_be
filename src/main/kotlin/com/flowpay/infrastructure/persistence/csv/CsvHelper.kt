package com.flowpay.infrastructure.persistence.csv

import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * CSV読み書きヘルパー
 * スレッドセーフなCSVファイル操作を提供
 */
@Component
class CsvHelper {
    private val locks = mutableMapOf<String, ReentrantReadWriteLock>()

    /**
     * CSVファイルを読み込む
     *
     * @param filePath ファイルパス
     * @return CSVの行リスト（ヘッダー含む）
     */
    fun readCsv(filePath: String): List<List<String>> {
        val lock = getLock(filePath)
        return lock.read {
            val file = File(filePath)
            if (!file.exists()) {
                return@read emptyList()
            }

            file.readLines(StandardCharsets.UTF_8).map { line ->
                parseCsvLine(line)
            }
        }
    }

    /**
     * CSVファイルに書き込む
     *
     * @param filePath ファイルパス
     * @param rows CSVの行リスト（ヘッダー含む）
     */
    fun writeCsv(filePath: String, rows: List<List<String>>) {
        val lock = getLock(filePath)
        lock.write {
            val file = File(filePath)

            // ディレクトリが存在しない場合は作成
            file.parentFile?.mkdirs()

            // 一時ファイルに書き込んでから置き換え（原子性を保証）
            val tempFile = File.createTempFile("csv_", ".tmp", file.parentFile)
            try {
                tempFile.bufferedWriter(StandardCharsets.UTF_8).use { writer ->
                    rows.forEach { row ->
                        writer.write(formatCsvLine(row))
                        writer.newLine()
                    }
                }
                Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                tempFile.delete()
                throw e
            }
        }
    }

    /**
     * CSVファイルをマップのリストとして読み込む
     * ヘッダー行をキーとして使用
     *
     * @param filePath ファイルパス
     * @return マップのリスト
     */
    fun readCsvAsMap(filePath: String): List<Map<String, String>> {
        val rows = readCsv(filePath)
        if (rows.isEmpty()) {
            return emptyList()
        }

        val headers = rows.first()
        return rows.drop(1).map { row ->
            headers.zip(row).toMap()
        }
    }

    /**
     * マップのリストをCSVファイルに書き込む
     *
     * @param filePath ファイルパス
     * @param headers ヘッダー行
     * @param data マップのリスト
     */
    fun writeCsvFromMap(filePath: String, headers: List<String>, data: List<Map<String, String>>) {
        val rows = mutableListOf<List<String>>()
        rows.add(headers)

        data.forEach { map ->
            val row = headers.map { header -> map[header] ?: "" }
            rows.add(row)
        }

        writeCsv(filePath, rows)
    }

    /**
     * CSV行をパース
     * シンプルなカンマ区切り（Phase 1では引用符やエスケープは未対応）
     */
    private fun parseCsvLine(line: String): List<String> {
        return line.split(",")
    }

    /**
     * CSV行をフォーマット
     */
    private fun formatCsvLine(row: List<String>): String {
        return row.joinToString(",")
    }

    /**
     * ファイルパスに対応するロックを取得
     */
    private fun getLock(filePath: String): ReentrantReadWriteLock {
        return synchronized(locks) {
            locks.getOrPut(filePath) { ReentrantReadWriteLock() }
        }
    }
}
