using System.ComponentModel.DataAnnotations;

namespace VinheriaAgnelloCRUD.DTOs.Wines;

/// <summary>
/// Dados necessarios para cadastrar um vinho.
/// </summary>
public class CreateWineRequest
{
    [Required(ErrorMessage = "O nome do vinho e obrigatorio.")]
    [MaxLength(120, ErrorMessage = "O nome deve ter no maximo 120 caracteres.")]
    public string Name { get; set; } = string.Empty;

    [Required(ErrorMessage = "A vinicola e obrigatoria.")]
    [MaxLength(80, ErrorMessage = "A vinicola deve ter no maximo 80 caracteres.")]
    public string Winery { get; set; } = string.Empty;

    [Required(ErrorMessage = "O tipo do vinho e obrigatorio.")]
    [MaxLength(60, ErrorMessage = "O tipo deve ter no maximo 60 caracteres.")]
    public string Type { get; set; } = string.Empty;

    [MaxLength(80, ErrorMessage = "O pais deve ter no maximo 80 caracteres.")]
    public string? Country { get; set; }

    [Range(1900, 2100, ErrorMessage = "O ano deve estar entre 1900 e 2100.")]
    public int? Year { get; set; }

    [Range(0, int.MaxValue, ErrorMessage = "A quantidade nao pode ser negativa.")]
    public int Quantity { get; set; }

    [Range(0, double.MaxValue, ErrorMessage = "O preco nao pode ser negativo.")]
    public decimal Price { get; set; }
}
